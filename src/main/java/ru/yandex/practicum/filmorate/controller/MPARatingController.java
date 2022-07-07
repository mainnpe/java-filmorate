package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.MPARatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPARatingController {
    private final FilmService filmService;

    @GetMapping
    public Collection<MPARating> findAllMPARatings() {
        return filmService.findAllRatings();
    }

    @GetMapping("/{id}")
    public MPARating findMPARating(@PathVariable Integer id) throws MPARatingNotFoundException {
        return filmService.findRating(id);
    }
}
