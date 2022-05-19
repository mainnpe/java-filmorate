package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    public static final LocalDate EARLIEST_RELEASE_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);
    private Map<Integer, Film> films = new HashMap<>();
    private int filmUniqueId = 1;

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Количество фильмов - {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        if (!validate(film)) {
            log.warn("Ошибка при создании фильма");
            throw new ValidationException("Ошибка при создании фильма");
        }
        film.setId(filmUniqueId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм - {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (!validate(film)) {
            log.warn("Ошибка при обновлении информации о фильме");
            throw new ValidationException("Ошибка при обновлении информации о фильме");
        }
        if(!films.containsKey(film.getId())) {
            log.warn("Информации о фильме {} не существует", film);
            throw new ValidationException("Ошибка при обновлении информации " +
                    "о фильме.");
        }
        films.put(film.getId(), film);
        log.info("Обновлена информация о фильме - {}", film);
        return film;
    }

    public static boolean validate(Film film) {
        return !(  film.getName().isBlank() //    название не может быть пустым;
                || film.getDescription().length() > 200 //максимальная длина описания — 200 символов
                || film.getReleaseDate().isBefore(
                        EARLIEST_RELEASE_DATE) //дата релиза — не раньше 28 декабря 1895 года
                || film.getDuration() < 0); //продолжительность фильма должна быть положительной

    }


}

