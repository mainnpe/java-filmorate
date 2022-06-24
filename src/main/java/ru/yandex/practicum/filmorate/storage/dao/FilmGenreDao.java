package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Repository
public class FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FilmGenre findGenre(Integer id) {
        try {
            String sql = "select * from film_genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<FilmGenre> findAllGenres() {
        String sql = "select * from film_genres";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    public Collection<FilmGenre> findFilmGenres(Integer id) {
        String sql = "select fgr.genre_id, fg.genre_name " +
                "from film_genre_rel fgr " +
                "join film_genres fg on fg.genre_id = fgr.genre_id " +
                "where film_id = ?";

        return jdbcTemplate.query(sql, this::makeGenre, id);
    }

    public void addFilmGenres(Film film) {
        Set<FilmGenre> genres = film.getGenres();

        if (!(genres == null || genres.isEmpty())) {
            String sqlQuery = "insert into film_genre_rel values (?,?)";
            genres.stream().forEach(x ->
                    jdbcTemplate.update(sqlQuery, film.getId(), x.getId()));
        }
    }

    public void updateFilmGenres(Film film) {
        Set<FilmGenre> newGenres = film.getGenres();
        Set<FilmGenre> currentGenres = new HashSet<>(findFilmGenres(film.getId()));

        if (!Objects.equals(newGenres, currentGenres)) {
            deleteFilmGenres(film);
            addFilmGenres(film);
        }
    }

    public void deleteFilmGenres(Film film) {
        String sql = "delete from film_genre_rel where film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private FilmGenre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String  name = rs.getString("genre_name");
        return new FilmGenre(id, name);
    }
}
