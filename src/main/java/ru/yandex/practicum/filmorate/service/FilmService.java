package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.validator.FilmValidators;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        FilmValidators.filmExistsValidator(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        return filmStorage.findFilm(id);
    }

    public Film addFilm(Film film) throws ValidationException {
        if (!FilmValidators.filmFormatValidator(film)) {
            log.warn("Ошибка при создании фильма");
            throw new ValidationException("Ошибка при создании фильма");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, FilmNotFoundException {
        if (!FilmValidators.filmFormatValidator(film)) {
            log.warn("Ошибка при обновлении информации о фильме");
            throw new ValidationException("Ошибка при обновлении информации о фильме");
        }
        FilmValidators.filmExistsValidator(filmStorage, film.getId(), String.format(
                "Фильм с id = %s не существует.", film.getId()), log);
        return filmStorage.updateFilm(film);
    }

    public void like(Integer id, Integer userId)
            throws UserNotFoundException, FilmNotFoundException, ValidationException
    {
        FilmValidators.filmExistsValidator(filmStorage, id,
                String.format("Фильм с id = %s не существует.", id), log);
        UserValidators.userExistsValidator(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", id), log);

        final Film film = filmStorage.findFilm(id);
        film.like(userId);
        filmStorage.updateFilm(film);
    }

    public void disLike(Integer id, Integer userId)
            throws ValidationException, FilmNotFoundException, UserNotFoundException
    {
        FilmValidators.filmExistsValidator(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        UserValidators.userExistsValidator(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", id), log);

        final Film film = filmStorage.findFilm(id);
        film.disLike(userId);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        return filmStorage.findAllFilms().stream()
                .sorted((o1,o2) -> Integer.compare(o2.getLikes().size(),
                            o1.getLikes().size())
                ).limit(count.orElse(10))
                .collect(Collectors.toList());
    }

}
