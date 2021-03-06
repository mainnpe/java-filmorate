package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film findFilm(Integer id);

    Collection<Film> findFilms(List<Integer> ids);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void like(Integer id, Integer userId);

    void disLike(Integer id, Integer userId);

    Collection<Film> findNMostPopularFilms(Optional<Integer> count);

    void deleteFilm(int id);

    Map<Integer, List<Integer>> getAllFilmsLikes();

    Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year);

    Collection<Film> findCommonFilmsByUsersIds(int userId, int friendId);

    Collection<Film> searchByName(String query);

    Collection<Film> searchByDirector(String query);

    Collection<Film> findFilmsOfDirectorSortByYear(Integer director_id);

    Collection<Film> findFilmsOfDirectorSortByLikes(Integer director_id);

    Collection<Film> searchByNameAndDirector(String query);
}
