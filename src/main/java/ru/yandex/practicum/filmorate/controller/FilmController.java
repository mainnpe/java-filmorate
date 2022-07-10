package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
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

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Integer id) throws FilmNotFoundException {
        return filmService.findFilm(id);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, FilmNotFoundException {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Integer id,
                         @PathVariable Integer userId)
            throws UserNotFoundException, FilmNotFoundException
    {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void disLikeFilm(@PathVariable Integer id,
                         @PathVariable Integer userId)
            throws FilmNotFoundException, UserNotFoundException
    {
        filmService.disLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id)
            throws FilmNotFoundException
    {
        filmService.deleteFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> findFilmsDirectorSortByYear(@PathVariable Integer directorId,
                                                        @RequestParam String sortBy)
            throws DirectorNotFoundException {
        return filmService.findFilmsOfDirector(directorId, sortBy);
    }

    @GetMapping(value = "/popular")
    public ResponseEntity<Collection<Film>> findMostPopularFilmsByGenreAndYear (
            @Positive
            @RequestParam(name="count", defaultValue = "10") int count,
            @RequestParam(name="genreId", defaultValue = "-1") int genreId,
            @RequestParam(name="year", defaultValue = "-1") int year)
        throws GenreNotFoundException, ValidationException {
        return ResponseEntity.ok(filmService.findMostPopularFilmsByGenreAndYear(count, genreId, year));
    }

    @GetMapping(value = "/common")
    public ResponseEntity<Collection<Film>> findCommonFilmsByUsersIds (
            @Positive
            @RequestParam(name = "userId") int userId,
            @Positive
            @RequestParam(name = "friendId") int friendId
    ) throws UserNotFoundException {
        return ResponseEntity.ok(filmService.findCommonFilmsByUsersIds(userId, friendId));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Collection<Film>> searchFilmsByQuery(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "by") List<String> by)
            throws GenreNotFoundException, ValidationException {
        return ResponseEntity.ok(filmService.searchFilm(query, by));
    }

}

