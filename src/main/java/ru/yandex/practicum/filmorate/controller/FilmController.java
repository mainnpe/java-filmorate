package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    //GET .../users/{id}
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Integer id) throws FilmNotFoundException {
        return filmService.findFilm(id);
    }

    //GET /films/popular?count={count}
    @GetMapping("/popular")
    public Collection<Film> findNMostPopularFilms(@RequestParam Optional<Integer> count)
    {
        return filmService.findNMostPopularFilms(count);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, FilmNotFoundException {
        return filmService.updateFilm(film);
    }

    //PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Integer id,
                         @PathVariable Integer userId)
            throws UserNotFoundException, FilmNotFoundException
    {
        filmService.like(id, userId);
    }

    //DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void disLikeFilm(@PathVariable Integer id,
                         @PathVariable Integer userId)
            throws FilmNotFoundException, UserNotFoundException
    {
        filmService.disLike(id, userId);
    }

}

