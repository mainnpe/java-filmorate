package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;

@Component
public class DirectorValidators {
    public boolean validateFormat(Director director) {
        return !director.getName().isBlank();
    }

    public void isDirectorExists(DirectorDao storage, Integer director_id,
                                        String message, Logger log) throws DirectorNotFoundException {
        if (storage.findDirector(director_id) == null) {
            log.warn(message);
            throw new DirectorNotFoundException(message);
        }
    }
}
