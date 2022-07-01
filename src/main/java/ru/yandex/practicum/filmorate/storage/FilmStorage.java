package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film findFilm(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void like(Integer id, Integer userId);

    void disLike(Integer id, Integer userId);

    Collection<Film> findNMostPopularFilms(Optional<Integer> count);

    Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year);

}
