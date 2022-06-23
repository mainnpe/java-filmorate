package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAllUsers() {
        String sql = "select * from users order by id";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User addUser(User user) {
        String sql = "insert into users (email, login, name, birthday)" +
                        "values (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String name = (user.getName() == null || user.getName().isEmpty())
                ? user.getLogin() : user.getName();

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, name);
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        if (rows == 1) {
            int id = keyHolder.getKey().intValue();
            return findUser(id);
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        String sql = "update users set email = ?, login = ?, " +
                "name = ?, birthday = ? where id = ?";

        int rows = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (rows == 1) {
            return findUser(user.getId());
        }
        return null;
    }

    @Override
    public User findUser(Integer id) {
        try {
            String sql = "select * from users where id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<User> findFriends(Integer id) {
        String sql = "select * from users where id in " +
                "(select friend_id from friends where user_id = ?)";
        return jdbcTemplate.query(sql, this::makeUser, id);
    }

    public Collection<User> findCommonFriends(Integer id, Integer otherId) {
        String sql = "select * from users where id in " +
                "(select distinct friend_id from friends " +
                "where user_id in (?, ?)" +
                "and friend_id not in (?, ?))";
        return jdbcTemplate.query(sql, this::makeUser, id, otherId, id, otherId);
    }

    public void addFriend(Integer id, Integer otherId) {
        String sql = "insert into friends (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sql, id, otherId);
        //Update friendship status
        updateFriendshipStatus(id, otherId, true);
    }

    public void deleteFriend(Integer id, Integer otherId) {
        String sql = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sql, id, otherId);
        //Update friendship status to false
        updateFriendshipStatus(id, otherId,false);
    }

    //Change friendship status if the friendship mutual
    //senderId is friend of recipientId and vice versa
    private void updateFriendshipStatus(Integer senderId, Integer recipientId, boolean status) {
        Collection<User> recipientFriends = findFriends(recipientId);

        if (!recipientFriends.isEmpty() && recipientFriends.contains(senderId)) {

            String query = "update friends set status = ? " +
                    "where (user_id = ? and friend_id = ?) " +
                    "or (user_id = ? and friend_id = ?)";

            jdbcTemplate.update(query, status, senderId, recipientId,
                    recipientId, senderId);

        }
    }


    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id,email,login,name,birthday, new HashSet<>());
    }
}
