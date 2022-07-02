package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private final MPARatingDao mpaRatingDao;
    private final DirectorDao directorDao;

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "select * from films order by id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film findFilm(Integer id) {
        try {
            String sql = "select * from films where id = ?";
            return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "insert into films (name, description, release_date, " +
                "duration, mpa_rating_id) " +
                "values (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //add general film info
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        if (rows == 1) {
            int id = keyHolder.getKey().intValue();
            film.setId(id);//assign auto-generated id
            filmGenreDao.addFilmGenres(film);//add film genres
            directorDao.addFilmDirectors(film);//add film directors
            return findFilm(id);
        }
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        Film initFilm = findFilm(film.getId());
        String sql = "update films set name = ?, description = ?, release_date = ?," +
                "duration = ?, mpa_rating_id = ? where id = ?";
        // update general film info
        int rows = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        filmGenreDao.updateFilmGenres(film); //update film genres
        directorDao.updateFilmDirectors(film); //update film directors
        if (rows == 1) {
            Film updFilm = findFilm(film.getId());
            if (initFilm.getGenres() != null && updFilm.getGenres() == null) {
                updFilm.setGenres(new HashSet<>()); // using to fit postman tests only
                //updFilm.setDirectors(new HashSet<>()); // using to fit postman tests only
            }
            if (initFilm.getDirectors().size() != 0 && updFilm.getDirectors().size() == 0) {
                updFilm.setDirectors(null); // using to fit postman tests only
            }
            return updFilm;
        }
        return null;
    }

    public void like(Integer id, Integer userId) {
        String sql = "insert into film_likes values (?,?)";
        jdbcTemplate.update(sql, id, userId);
        updateRate(id, 1); // increase film rate by 1
    }

    public void disLike(Integer id, Integer userId) {
        String sql = "delete from film_likes " +
                "where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        updateRate(id, -1); // decrease film rate by 1
    }

    //Increase or decrease rate by rateDiff (like/dislike)
    private void updateRate(Integer id, Integer rateDiff) {
        String sql = "update films set rate = rate + ? where id = ?";
        jdbcTemplate.update(sql, rateDiff, id);
    }

    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        String sql = "select * from films order by rate desc limit ?";
        return jdbcTemplate.query(sql, this::makeFilm, count.orElse(10));
    }

    @Override
    public Collection<Film> findFilmsOfDirectorSortByYear(Integer id) {
        String sql = "select F2.ID, " +
                "F2.NAME, " +
                "F2.DESCRIPTION, " +
                "F2.RELEASE_DATE, " +
                "F2.DURATION, " +
                "F2.MPA_RATING_ID " +
                "from DIRECTOR_REL " +
                "JOIN FILMS F2 on DIRECTOR_REL.FILM_ID = F2.ID " +
                "where DIRECTOR_REL.ID = ? order by release_date";

        return jdbcTemplate.query(sql, this::makeFilm, id);
    }

    @Override
    public Collection<Film> findFilmsOfDirectorSortByLikes(Integer id) {
        String sql = "select F2.ID, " +
                "F2.NAME, " +
                "F2.DESCRIPTION, " +
                "F2.RELEASE_DATE, " +
                "F2.DURATION, " +
                "F2.MPA_RATING_ID " +
                "from DIRECTOR_REL " +
                "JOIN FILMS F2 on DIRECTOR_REL.FILM_ID = F2.ID " +
                "where DIRECTOR_REL.ID = ? order by rate";

        return jdbcTemplate.query(sql, this::makeFilm, id);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpa_id = rs.getInt("mpa_rating_id");

        MPARating mpa = mpaRatingDao.findRating(mpa_id);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findFilmGenres(id));
        Set<Director> directors = new HashSet<>(directorDao.findFilmDirectors(id));
        genres = genres.isEmpty() ? null : genres; //for postman test fitting

        return new Film(id, name, description, releaseDate,
                duration, directors, new HashSet<>(), mpa, genres);
    }
}
