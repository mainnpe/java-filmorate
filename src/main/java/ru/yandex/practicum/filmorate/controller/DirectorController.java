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

    @GetMapping("/directors")
    public Collection<Director> findAll() {
        return filmService.findAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director findById(@PathVariable Integer id) throws DirectorNotFoundException {
        return filmService.findDirector(id);
    }

    @PostMapping("/directors")
    public Director add(@RequestBody Director director) throws ValidationException {
        return filmService.addDirector(director);
    }

    @PutMapping("/directors")
    public Director update(@RequestBody Director director)
            throws ValidationException, DirectorNotFoundException {
        return filmService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public void delete(@PathVariable Integer id) throws DirectorNotFoundException {
        filmService.deleteDirector(id);
    }
}
