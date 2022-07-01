package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.validator.FilmValidators;
import ru.yandex.practicum.filmorate.service.validator.ReviewValidator;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review create(Review review) throws ValidationException, NotFoundException {
        if (!ReviewValidator.validateFormat(review)) {
            log.warn("Ошибка при создании отзыва");
            throw new ValidationException("Ошибка при создании отзыва");
        }

        FilmValidators.isExists(filmStorage, review.getFilmId(),
                String.format("Фильм с id = %s не существует.", review.getFilmId()), log);
        UserValidators.isExists(userStorage, review.getUserId(), String.format(
                "Пользователь с id = %s не существует.", review.getUserId()), log);

        Review save = reviewStorage.save(review);

        log.info("Отзыв {} сохранен", save);
        return save;
    }

    public Review getById(Integer id) throws ReviewNotFoundException {
        ReviewValidator.isExists(reviewStorage, id, String.format(
                "Отзыв с id = %s не существует.", id), log);
        Review review = reviewStorage.findById(id);
        log.info("Отзыв с id  {} найден", review);
        return review;
    }

    public Collection<Review> getAll(Optional<Integer> filmId, Optional<Integer> count) {
        Collection<Review> all;
        if(filmId.isPresent())
            all = reviewStorage.findAll(filmId.get(), count.orElse(10));
        else
            all = reviewStorage.findAll(count.orElse(10));

        log.info("Найдены отзывы по фильму с id {} в количестве {} : {}", filmId, count, all);
        return all;
    }

    public void update(Review review) {
        reviewStorage.update(review);
        log.info("Отзыв {} обновлен", review);
    }

    public void deleteById(Integer id) {
        if (reviewStorage.deleteById(id)) {
            log.info("Отзыв с id {} удален", id);
        } else {
            log.info("Отзыв с id {} не удален", id);
        }
    }

    public void addLike(Integer id, Integer userId) {
        reviewStorage.addLike(userId, id, 1);
        log.info("Добавлен дислайк пользователем с id {} к отзыву с id {}", userId, id);
    }

    public void addDislike(Integer id, Integer userId) {
        reviewStorage.addLike(userId, id, -1);
        log.info("Добавлен лайк пользователем с id {} к отзыву с id {}", userId, id);
    }

    public void removeLike(Integer id, Integer userId) {
        reviewStorage.removeLike(userId, id, 1);
        log.info("Удален лайк пользователем с id {} у отзыву с id {}", userId, id);
    }

    public void removeDislike(Integer id, Integer userId) {
        reviewStorage.removeLike(userId, id, -1);
        log.info("Удален дислайк пользователем с id {} у отзыву с id {}", userId, id);
    }
}
