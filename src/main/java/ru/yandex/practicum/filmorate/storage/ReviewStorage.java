package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    Review save(Review review);

    Review findById(int id);

    Collection<Review> findAll(int filmId, int limit);

    Collection<Review> findAll(int limit);

    int update(Review review);

    boolean deleteById(int id);

    void addLike(int userId, int reviewId, int score);

    boolean removeLike(int userId, int reviewId, int score);

    boolean removeLike(int reviewId);
}
