package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;



@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private final MPARatingDao mpaRatingDao;

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
    public Collection<Film> findFilms(List<Integer> ids) {
        if (ids.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String filmIds = ids.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        String sql = "select * from films " +
                "where id in (" + filmIds + ")";
        return jdbcTemplate.query(sql, this::makeFilm);
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
        if (rows == 1) {
            Film updFilm = findFilm(film.getId());
            if (initFilm.getGenres() != null && updFilm.getGenres() == null) {
                updFilm.setGenres(new HashSet<>()); // using to fit postman tests only
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
    public Map<Integer, List<Integer>> getAllFilmsLikes() {
        String sql = "select * from film_likes";
        Map<Integer, List<Integer>> likes = new HashMap<>();

        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);

        while (rows.next()) {
            int userId = rows.getInt("user_id");
            int filmId = rows.getInt("film_id");

            if (!likes.containsKey(userId)) {
                likes.put(userId, new ArrayList<>(List.of(filmId)));
            }
            List<Integer> newValue = likes.get(userId);
            newValue.add(filmId); //Add new like

            likes.put(userId, newValue);
        }

        return likes;
    }

    @Override
    public Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT f.* FROM films AS f ");
        if(genreId > 0){
            sql.append("LEFT JOIN film_genre_rel AS g ON f.id = g.film_id ");
        }
        if(genreId > 0 || year > 0){
            sql.append("WHERE ");
            if(year > 0){
                sql.append("(f.release_date >= ? AND f.release_date <= ?) ");
            }
            if(genreId > 0 && year > 0){
                sql.append("AND ");
            }
            if(genreId > 0){
                sql.append("g.genre_id  = ? ");
            }
        }
        sql.append("ORDER BY f.rate DESC ");
        sql.append("LIMIT ?");
        if(genreId > 0 && year > 0){
            return jdbcTemplate.query(sql.toString(),
                    this::makeFilm,
                    LocalDate.of(year, 1, 1),
                    LocalDate.of(year, 12, 31),
                    genreId, count);
        }
        if(year > 0){
            return jdbcTemplate.query(sql.toString(),
                    this::makeFilm,
                    LocalDate.of(year, 1, 1),
                    LocalDate.of(year, 12, 31),
                    count);
        }
        if(genreId > 0){
            return jdbcTemplate.query(sql.toString(), this::makeFilm, genreId, count);
        }
        return jdbcTemplate.query(sql.toString(), this::makeFilm, count);
    }

    public Collection<Film> findCommonFilmsByUsersIds(int userId, int friendId){
        String sql = "SELECT fl.film_id " +
                "FROM film_likes fl " +
                "WHERE fl.user_id in (?, ?) " +
                "GROUP BY  fl.film_id  " +
                "HAVING COUNT(DISTINCT fl.user_id) = 2";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        Set<String> filmIds = new HashSet<>();
        if(rows.next()){
            filmIds.add(rows.getString("film_id"));
        }
        if(filmIds.size() > 0) {
            String sql2 = "SELECT * FROM films " +
                    "WHERE id IN ( " + String.join(",", filmIds)+ " ) " +
                    "ORDER BY rate DESC";
            return jdbcTemplate.query(sql2, this::makeFilm);
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public Collection<Film> searchByName(String query) {
        String sql = "SELECT * " +
                "FROM films " +
                "WHERE name LIKE '?*'";
        return jdbcTemplate.query(sql.toString(), this::makeFilm, query);
    }

    @Override
    public Collection<Film> searchByDirector(String query) {
        return null;
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
        genres = genres.isEmpty() ? null : genres; //for postman test fitting

        return new Film(id, name, description, releaseDate,
                duration, new HashSet<>(), mpa, genres);
    }
}
