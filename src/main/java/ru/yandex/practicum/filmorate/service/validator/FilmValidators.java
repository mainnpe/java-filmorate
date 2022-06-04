package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;

public class FilmValidators {
    public static final LocalDate EARLIEST_RELEASE_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);

    public static boolean validateFormat(Film film) {
        return !(  film.getName().isBlank() //    название не может быть пустым;
                || film.getDescription().length() > 200 //максимальная длина описания — 200 символов
                || film.getReleaseDate().isBefore(
                EARLIEST_RELEASE_DATE) //дата релиза — не раньше 28 декабря 1895 года
                || film.getDuration() < 0); //продолжительность фильма должна быть положительной

    }

    public static void isExists(FilmStorage storage, Integer id,
                                String message, Logger log) throws FilmNotFoundException {
        if (storage.findFilm(id) == null) {
            log.warn(message);
            throw new FilmNotFoundException(message);
        }
    }
}