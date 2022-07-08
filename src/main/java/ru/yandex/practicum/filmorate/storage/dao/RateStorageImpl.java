package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.interfaces.RateStorage;
import ru.yandex.practicum.filmorate.model.Rate;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.OptionalDouble;

@Repository
@Primary
public class RateStorageImpl implements RateStorage {

    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    private final String INSERT_RATE = "INSERT INTO film_rates VALUES (?,?,?)";
    private final String DELETE_RATE = "DELETE FROM film_rates " +
            "WHERE film_id = ? AND user_id = ?";
    private final String SELECT_RATE_BY_USER_AND_FILM_ID = "SELECT * FROM film_rates" +
            "WHERE film_id = ? AND user_id = ?";

    public RateStorageImpl(FilmStorage filmStorage, JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addRate(Integer id, Integer userId, Float rate) {
        jdbcTemplate.update(INSERT_RATE, id, userId, rate);
        updateRateByUsers(id);
    }

    @Override
    public void removeRate(Integer id, Integer userId) {
        jdbcTemplate.update(DELETE_RATE, id, userId);
        updateRateByUsers(id);
    }

    private void updateRateByUsers(Integer id) {
        OptionalDouble rate = null;
        if(filmStorage.findFilm(id).getRates().size() > 0){
            rate = filmStorage.findFilm(id).getRates().stream().mapToDouble(Rate::getRate).average();
        }
        float calcrate = (float) rate.orElse(0);
        filmStorage.updateRate(id, calcrate);
    }

    @Override
    public boolean findByFilmAndUserId(int id, int filmId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_RATE_BY_USER_AND_FILM_ID, filmId, id);
        if(rows.next()){
            return true;
        } else {
            return false;
        }
    }
}
