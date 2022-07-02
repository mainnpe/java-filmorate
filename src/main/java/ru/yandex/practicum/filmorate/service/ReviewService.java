package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEventType;
import ru.yandex.practicum.filmorate.model.eventmanager.UserOperation;
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
    private final EventManager eventManager;

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

        eventManager.register(new UserEvent(
                save.getUserId(),
                save.getId(),
                UserEventType.REVIEW,
                UserOperation.ADD
        ));

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
        if(reviewStorage.update(review) == 1) {
            log.info("Отзыв {} обновлен", review);

            Review byId = reviewStorage.findById(review.getId());

            eventManager.register(new UserEvent(
                    byId.getUserId(),
                    byId.getId(),
                    UserEventType.REVIEW,
                    UserOperation.UPDATE
            ));
        }
    }

    public void deleteById(Integer id) {
        Review review = reviewStorage.findById(id);
        if (reviewStorage.deleteById(id)) {
            log.info("Отзыв с id {} удален", id);

            eventManager.register(new UserEvent(
                    review.getUserId(),
                    review.getId(),
                    UserEventType.REVIEW,
                    UserOperation.REMOVE
            ));
        } else {
            log.info("Отзыв с id {} не удален", id);
        }
    }

    public void addLike(Integer id, Integer userId) {
        reviewStorage.addLike(userId, id, 1);
        log.info("Добавлен лайк пользователем с id {} к отзыву с id {}", userId, id);
    }

    public void addDislike(Integer id, Integer userId) {
        reviewStorage.addLike(userId, id, -1);
        log.info("Добавлен дизлайк пользователем с id {} к отзыву с id {}", userId, id);
    }

    public void removeLike(Integer id, Integer userId) {
        reviewStorage.removeLike(userId, id, 1);
        log.info("Удален лайк пользователем с id {} у отзыву с id {}", userId, id);
    }

    public void removeDislike(Integer id, Integer userId) {
        reviewStorage.removeLike(userId, id, -1);
        log.info("Удален дизлайк пользователем с id {} у отзыву с id {}", userId, id);
    }
}
