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
            String sql = "SELECT * FROM director WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeDirector, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<Director> findAllDirector() {
        String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, this::makeDirector);
    }

    public Director addDirector(Director director) {
        String sql = "INSERT INTO director (director_name) VALUES (?)";
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
        String sql = "UPDATE director SET director_name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    public void addFilmDirectors(Film film) {
        Set<Director> directors = film.getDirectors();

        if (!(directors == null || directors.isEmpty())) {
            String sqlQuery = "INSERT INTO director_rel VALUES (?,?)";
            directors.stream().forEach(x ->
                    jdbcTemplate.update(sqlQuery, film.getId(), x.getId()));
        }
    }

    public Collection<Director> findFilmDirectors(Integer id) {
        String sql = "SELECT director_rel.id, director.director_name " +
                "FROM director_rel " +
                "JOIN director ON director.id = director_rel.id " +
                "WHERE film_id = ?";

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
        String sql = "DELETE FROM director_rel WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    public void deleteDirectorsFromFilm(Integer id) {
        String sql = "DELETE FROM director_rel WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteDirectors(Integer id) {
        String sql = "DELETE FROM director WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String  name = rs.getString("director_name");
        return new Director(id, name);
    }
}
