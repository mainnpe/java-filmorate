package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEventType;
import ru.yandex.practicum.filmorate.model.eventmanager.UserOperation;
import ru.yandex.practicum.filmorate.service.validator.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MPARatingDao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreDao genreStorage;
    private final MPARatingDao mpaRatingStorage;
    private final DirectorDao directorStorage;
    private final EventManager eventManager;
    private final UserValidators userValidator;
    private final FilmValidators filmValidator;
    private final DirectorValidators directorValidator;

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
        filmValidator.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        return filmStorage.findFilm(id);
    }

    public Film addFilm(Film film) throws ValidationException {
        if (!filmValidator.validateFormat(film)) {
            log.warn("Ошибка при создании фильма");
            throw new ValidationException("Ошибка при создании фильма");
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, FilmNotFoundException {
        if (!filmValidator.validateFormat(film)) {
            log.warn("Ошибка при обновлении информации о фильме");
            throw new ValidationException("Ошибка при обновлении информации о фильме");
        }
        filmValidator.isExists(filmStorage, film.getId(), String.format(
                "Фильм с id = %s не существует.", film.getId()), log);
        return filmStorage.updateFilm(film);
    }

    public void like(Integer id, Integer userId)
            throws UserNotFoundException, FilmNotFoundException
    {
        filmValidator.isExists(filmStorage, id,
                String.format("Фильм с id = %s не существует.", id), log);
        userValidator.isExists(userStorage, userId, String.format(
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
            throws FilmNotFoundException, UserNotFoundException
    {
        filmValidator.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        userValidator.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);

        filmStorage.disLike(id, userId);

        eventManager.register(new UserEvent(
                userId,
                id,
                UserEventType.LIKE,
                UserOperation.REMOVE
        ));
    }

    public void deleteFilm(int id) throws FilmNotFoundException {
        filmValidator.isExists(filmStorage, id, String.format(
                "Фильм с id = %s не существует.", id), log);
        filmStorage.deleteFilm(id);
    }

    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        return filmStorage.findNMostPopularFilms(count);
    }

    public FilmGenre findGenre(Integer id) throws GenreNotFoundException {
        filmValidator.isGenreExists(genreStorage, id, String.format(
                "Жанр фильма с id = %s не существует.", id), log);
        return genreStorage.findGenre(id);
    }

    public Collection<FilmGenre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public MPARating findRating(Integer id) throws MPARatingNotFoundException {
        filmValidator.isMPARatingExists(mpaRatingStorage, id, String.format(
                "Рейтинга MPA с id = %s не существует.", id), log);
        return mpaRatingStorage.findRating(id);
    }

    public Collection<MPARating> findAllRatings() {
        return mpaRatingStorage.findAllRatings();
    }

    public Collection<Film> findCommonFilmsByUsersIds(int userId, int friendId) throws UserNotFoundException {
        userValidator.isExists(userStorage, userId, String.format(
                "Пользователь с id = %s не существует.", userId), log);
        userValidator.isExists(userStorage, friendId, String.format(
                "Пользователь с id = %s не существует.", userId), log);
        return filmStorage.findCommonFilmsByUsersIds(userId, friendId);
    }

    public Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year)
            throws GenreNotFoundException, ValidationException {
        if (genreId != -1) {
            filmValidator.isGenreExists(genreStorage, genreId, String.format(
                    "Жанр фильма с id = %s не существует.", genreId), log);
        }
        if (year < FilmValidators.EARLIEST_RELEASE_DATE.getYear() && year != -1) {
            throw new ValidationException("В этот год кино еще не снимали");
        }
        return filmStorage.findMostPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public Collection<Film> searchFilm(String query, List<String> fields) throws ValidationException {
        Collection<Film> result;
        if (fields.size() == 2) {
            result = filmStorage.searchByNameAndDirector(query);
        } else if (fields.get(0).equals("title")) {
            result = filmStorage.searchByName(query);
        } else if (fields.get(0).equals("director")){
            result = filmStorage.searchByDirector(query);
        } else {
            throw new ValidationException("Некорректные параметры запроса");
        }
        return result;
    }


    public Director findDirector(Integer director_id) throws DirectorNotFoundException {
        directorValidator.isDirectorExists(directorStorage, director_id, String.format(
                "Режиссёр с id = %s не существует.", director_id), log);
        return directorStorage.find(director_id);
    }
    public Collection<Director> findAllDirectors() {
        return directorStorage.findAll();
    }

    public Director addDirector(Director director) throws ValidationException {
        if (!directorValidator.validateFormat(director)) {
            log.warn("Ошибка при создании режиссёра");
            throw new ValidationException("Ошибка при создании режиссёра");
        }
        return directorStorage.add(director);
    }

    public Director updateDirector(Director director)
            throws ValidationException, DirectorNotFoundException {
        if (!directorValidator.validateFormat(director)) {
            log.warn("Ошибка при создании режиссёра");
            throw new ValidationException("Ошибка при создании режиссёра");
        }
        directorValidator.isDirectorExists(directorStorage, director.getId(), String.format(
                "Режиссёр с id = %s не существует.", director.getId()), log);
        return directorStorage.update(director);
    }

    public Collection<Film> findFilmsOfDirector(Integer id, String sortBy) throws DirectorNotFoundException {
        directorValidator.isDirectorExists(directorStorage, id, String.format(
                "Режиссёр с id = %s не существует.", id), log);
        if (sortBy.equals("likes")) {
            return filmStorage.findFilmsOfDirectorSortByLikes(id);
        } else if (sortBy.equals("year")) {
            return filmStorage.findFilmsOfDirectorSortByYear(id);
        }
        return null;
    }

    public void deleteDirector(Integer director_id) throws DirectorNotFoundException {
        directorValidator.isDirectorExists(directorStorage, director_id, String.format(
                "Режиссёр с id = %s не существует.", director_id), log);
        directorStorage.deleteFromFilm(director_id);
        directorStorage.delete(director_id);
    }
}