package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Repository
public class DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    public Director findDirector(Integer id) {
        try {
            String sql = "" +
                    "select * from director where id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeDirector, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<Director> findAllDirector() {
        String sql = "select * from director";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    public Director addDirector(Director director) {
        String sql = "insert into director (director_name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        if (rows == 1) {
            int id = keyHolder.getKey().intValue();
            return findDirector(id);
        }
        return null;
    }
    public Director updateDirector(Director director) {
        String sql = "update director set director_name = ? where id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    public void addFilmDirectors(Film film) {
        Set<Director> directors = film.getDirectors();

        if (!(directors == null || directors.isEmpty())) {
            String sqlQuery = "insert into director_rel values (?,?)";
            directors.stream().forEach(x ->
                    jdbcTemplate.update(sqlQuery, film.getId(), x.getId()));
        }
    }

    public Collection<Director> findFilmDirectors(Integer id) {
        String sql = "select director_rel.id, director.director_name " +
                "from director_rel " +
                "join director on director.id = director_rel.id " +
                "where film_id = ?";

        return jdbcTemplate.query(sql, this::makeDirector, id);
    }

    public void updateFilmDirectors(Film film) {
        Set<Director> newDirectors = film.getDirectors();
        Set<Director> currentDirector = new HashSet<>(findFilmDirectors(film.getId()));

        if (!Objects.equals(newDirectors, currentDirector)) {
            deleteFilmDirectors(film);
            addFilmDirectors(film);
        }
    }

    public void deleteFilmDirectors(Film film) {
        String sql = "delete from director_rel where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    public void deleteDirectorsFromFilm(Integer id) {
        String sql = "delete from director_rel where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteDirectors(Integer id) {
        String sql = "delete from director where id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String  name = rs.getString("director_name");
        return new Director(id, name);
    }
}
