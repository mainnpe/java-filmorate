package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MPARatingNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class AuxController {
    private final FilmService filmService;

    @GetMapping("/genres")
    public Collection<FilmGenre> findAllGenres() {
        return filmService.findAllGenres();
    }

    @GetMapping("/genres/{id}")
    public FilmGenre findGenre(@PathVariable Integer id) throws GenreNotFoundException {
        return filmService.findGenre(id);
    }

    @GetMapping("/mpa")
    public Collection<MPARating> findAllMPARatings() {
        return filmService.findAllRatings();
    }

    @GetMapping("/mpa/{id}")
    public MPARating findMPARating(@PathVariable Integer id) throws MPARatingNotFoundException {
        return filmService.findRating(id);
    }
}
