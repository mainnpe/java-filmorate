package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

public class ReviewValidator {

    public static boolean validateFormat(Review review) {
        return !( review.isPositive() == null
                || review.getFilmId() == null
                || review.getUserId() == null
                || review.getContent() == null
                || review.getContent().isEmpty()
                || review.getContent().isBlank());
    }

    public static void isExists(ReviewStorage storage, Integer id,
                                String message, Logger log) throws ReviewNotFoundException {
        if (storage.findById(id) == null) {
            log.warn(message);
            throw new ReviewNotFoundException(message);
        }
    }
}
