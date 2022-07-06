package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

public class DirectorValidators {
    public static boolean validateFormat(Director director) {
        return !director.getName().isBlank();
    }

    public static void isDirectorExists(DirectorDao storage, Integer director_id,
                                        String message, Logger log) throws DirectorNotFoundException {
        if (storage.find(director_id) == null) {
            log.warn(message);
            throw new DirectorNotFoundException(message);
        }
    }
}
