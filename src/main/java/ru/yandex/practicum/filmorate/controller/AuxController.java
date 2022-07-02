package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MPARatingNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
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

    //GET /directors
    @GetMapping("/directors")
    public Collection<Director> findAllDirectors() {
        return filmService.findAllDirectors();
    }

    //GET /directors/{id}
    @GetMapping("/directors/{id}")
    public Director findDirectorById(@PathVariable Integer id) throws DirectorNotFoundException {
        return filmService.findDirector(id);
    }

    //POST /directors
    @PostMapping("/directors")
    public Director addDirector(@RequestBody Director director) throws ValidationException {
        return filmService.addDirector(director);
    }

    //PUT /directors
    @PutMapping("/directors")
    public Director updateDirectors(@RequestBody Director director)
            throws ValidationException, DirectorNotFoundException {
        return filmService.updateDirector(director);
    }

    //DELETE /directors/{id}
    @DeleteMapping("/directors/{id}")
    public void deleteDirectors(@PathVariable Integer id) throws DirectorNotFoundException {
        filmService.deleteDirector(id);
    }
}
