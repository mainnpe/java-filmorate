package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.*;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review save(Review review) {
        String sqlQuery =
                "insert into reviews(film_id, user_id, is_positive, content) " +
                        "values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"id"});
                    preparedStatement.setInt(1, review.getFilmId());
                    preparedStatement.setInt(2, review.getUserId());
                    preparedStatement.setBoolean(3, review.isPositive());
                    preparedStatement.setString(4, review.getContent());
                    return preparedStatement;
                }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("Column \"id\" doesn't exist");
        }

        review.setId(key.intValue());
        return review;
    }

    @Override
    public Review findById(int id) {
        try {
            String sqlQuery =
                    "select id, film_id, user_id, is_positive, content, useful_rating " +
                            "from reviews r " +
                            "left join " +
                            "( select review_id, sum(score) useful_rating " +
                            "from review_scores " +
                            "group by review_id ) ur " +
                            "on r.id = ur.review_id " +
                            "where id = ? ";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<Review> findAll(int filmId, int limit) {
        String sqlQuery =
                "select id, film_id, user_id, is_positive, content, useful_rating " +
                "from reviews r " +
                "left join " +
                    "( select review_id, sum(score) useful_rating " +
                    "from review_scores " +
                    "group by review_id ) ur " +
                    "on r.id = ur.review_id " +
                "where film_id = ? " +
                "order by useful_rating desc " +
                "limit ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, limit);
    }

    @Override
    public Collection<Review> findAll(int limit) {
        String sqlQuery =
                "select r.id, r.film_id, r.user_id, r.is_positive, r.content, ur.useful_rating " +
                "from reviews as r " +
                "left join " +
                    "( select review_id, sum(score) as useful_rating " +
                    "from review_scores " +
                    "group by review_id ) as ur " +
                "on r.id = ur.review_id " +
                "order by case when useful_rating is null then 0 else useful_rating end desc " +
                "limit ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, limit);
    }

    @Override
    public int update(Review review) {
        String sqlQuery =
                "update reviews set " +
                "is_positive = ?, content = ? " +
                "where id = ?";

        return jdbcTemplate.update(sqlQuery,
                review.isPositive(),
                review.getContent(),
                review.getId());
    }

    @Override
    public boolean deleteById(int id) {
        removeLike(id);
        return jdbcTemplate.update("delete from reviews where id = ?", id) > 0;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .id(resultSet.getInt("id"))
                .filmId(resultSet.getInt("film_id"))
                .userId(resultSet.getInt("user_id"))
                .positive(resultSet.getBoolean("is_positive"))
                .content(resultSet.getString("content"))
                .useful(resultSet.getInt("ur.useful_rating"))
                .build();
    }

    @Override
    public void addLike(int userId, int reviewId, int score) {
        jdbcTemplate.update("insert into review_scores(user_id, review_id, score) values (?, ?, ?)",
                userId, reviewId, score);
    }

    @Override
    public boolean removeLike(int userId, int reviewId, int score) {
        return jdbcTemplate.update("delete from review_scores where user_id = ? and review_id = ? and score = ?",
                userId, score) > 0;
    }

    @Override
    public boolean removeLike(int reviewId) {
        return jdbcTemplate.update("delete from review_scores where review_id = ?",
                reviewId) > 0;
    }
}
