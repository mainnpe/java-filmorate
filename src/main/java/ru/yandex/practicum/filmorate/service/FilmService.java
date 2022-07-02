package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEventType;
import ru.yandex.practicum.filmorate.model.eventmanager.UserOperation;
import ru.yandex.practicum.filmorate.service.validator.FilmValidators;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MPARatingDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreDao genreStorage;
    private final MPARatingDao mpaRatingStorage;

    @Autowired
    private final EventManager eventManager;

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }


    public Film findFilm(Integer id) throws FilmNotFoundException {
        FilmValidators.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        return filmStorage.findFilm(id);
    }

    public Film addFilm(Film film) throws ValidationException {
        if (!FilmValidators.validateFormat(film)) {
            log.warn("Ошибка при создании фильма");
            throw new ValidationException("Ошибка при создании фильма");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, FilmNotFoundException {
        if (!FilmValidators.validateFormat(film)) {
            log.warn("Ошибка при обновлении информации о фильме");
            throw new ValidationException("Ошибка при обновлении информации о фильме");
        }
        FilmValidators.isExists(filmStorage, film.getId(), String.format(
                "Фильм с id = %s не существует.", film.getId()), log);
        return filmStorage.updateFilm(film);
    }

    public void like(Integer id, Integer userId)
            throws UserNotFoundException, FilmNotFoundException {
        FilmValidators.isExists(filmStorage, id,
                String.format("Фильм с id = %s не существует.", id), log);
        UserValidators.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);

        filmStorage.like(id, userId);

        eventManager.register(new UserEvent(
                userId,
                id,
                UserEventType.LIKE,
                UserOperation.ADD
        ));
    }

    public void disLike(Integer id, Integer userId)
            throws FilmNotFoundException, UserNotFoundException {
        FilmValidators.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        UserValidators.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);

        filmStorage.disLike(id, userId);

        eventManager.register(new UserEvent(
                userId,
                id,
                UserEventType.LIKE,
                UserOperation.REMOVE
        ));
    }

    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        return filmStorage.findNMostPopularFilms(count);
    }

    public FilmGenre findGenre(Integer id) throws GenreNotFoundException {
        FilmValidators.isGenreExists(genreStorage, id, String.format(
                "Жанр фильма с id = %s не существует.", id), log);
        return genreStorage.findGenre(id);
    }

    public Collection<FilmGenre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public MPARating findRating(Integer id) throws MPARatingNotFoundException {
        FilmValidators.isMPARatingExists(mpaRatingStorage, id, String.format(
                "Рейтинга MPA с id = %s не существует.", id), log);
        return mpaRatingStorage.findRating(id);
    }

    public Collection<MPARating> findAllRatings() {
        return mpaRatingStorage.findAllRatings();
    }

    public Collection<Film> findCommonFilmsByUsersIds(int userId, int friendId) throws UserNotFoundException {
        UserValidators.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);
        UserValidators.isExists(userStorage, friendId, String.format(
                "Пользователь с id = %s не существует.", userId), log);
        return filmStorage.findCommonFilmsByUsersIds(userId, friendId);
    }

    public Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year)
            throws GenreNotFoundException, ValidationException {
        if (genreId != -1) {
            FilmValidators.isGenreExists(genreStorage, genreId, String.format(
                    "Жанр фильма с id = %s не существует.", genreId), log);
        }
        if (year < FilmValidators.EARLIEST_RELEASE_DATE.getYear() && year != -1) {
            throw new ValidationException("В этот год кино еще не снимали");
        }
        return filmStorage.findMostPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public Collection<Film> searchFilm(String query, String fields) {
        Collection<Film> result = new ArrayList<>();
        for (String field : fields.split(",")) {
            Collection<Film> foundData;
            switch (field) {
                case "title":
                    foundData = filmStorage.searchByName(query);
                    if (foundData != null) {
                        result.addAll(foundData);
                    }
            }
        }
        return result;
    }

}
