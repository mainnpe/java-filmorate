package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.RateFoundException;
import ru.yandex.practicum.filmorate.interfaces.RateStorage;

public class RateValidators {
    public static void isExists(RateStorage storage, Integer id, Integer filmId,
                                String message, Logger log) throws RateFoundException {
        if (storage.findByFilmAndUserId(id, filmId)) {
            log.warn(message);
            throw new RateFoundException(message);
        }
    }
}
