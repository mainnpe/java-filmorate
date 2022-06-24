package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class MPARatingDao {
    private final JdbcTemplate jdbcTemplate;

    public MPARatingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MPARating findRating(Integer id) {
        try {
            String sql = "select * from mpa_age_ratings where rating_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeMPA, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<MPARating> findAllRatings() {
        String sql = "select * from mpa_age_ratings";
        return jdbcTemplate.query(sql, this::makeMPA);
    }

    private MPARating makeMPA(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("rating_id");
        String  name = rs.getString("rating_name");
        return new MPARating(id, name);
    }
}
