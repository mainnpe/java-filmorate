package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final FilmService filmService;

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
