package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review) throws ValidationException, NotFoundException {
        log.info("POST {}", review);
        return reviewService.create(review);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Integer id) throws ReviewNotFoundException {
        log.info("GET BY ID {}", id);
        return reviewService.getById(id);
    }

    @GetMapping()
    public Collection<Review> getAll(@RequestParam Optional<Integer> filmId, @RequestParam Optional<Integer> count) {
        log.info("GET ALL {} {}", filmId, count);
        return reviewService.getAll(filmId, count);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        log.info("PUT {}", review);
        reviewService.update(review);
        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        log.info("DELETE BY ID {}", id);
        reviewService.deleteById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("PUT LIKE FILM ID {} TO REVIEW ID {}", userId, id);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("PUT DISLIKE FILM ID {} TO REVIEW ID {}", userId, id);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("DELETE LIKE FILM ID {} TO REVIEW ID {}", userId, id);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("DELETE LIKE FILM ID {} TO REVIEW ID {}", userId, id);
        reviewService.removeDislike(id, userId);
    }
}
