package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Rate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class FilmRatesDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmRatesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Rate> getFilmRates(Integer id) {
        String sql = "SELECT * " +
                "FROM film_rates " +
                "WHERE film_id = ?";

        return jdbcTemplate.query(sql, this::makeRate, id);
    }

    private Rate makeRate(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("film_id");
        Integer uid = rs.getInt("user_id");
        Float rate = rs.getFloat("rate");
        return new Rate(id, uid, rate);
    }

}
