package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.FilmValidators;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    FilmService service;

    @BeforeEach
    void beforeEach() {
        service = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryUserStorage()
        );
    }

    //    название не может быть пустым;
//    максимальная длина описания — 200 символов;
//    дата релиза — не раньше 28 декабря 1895 года;
//    продолжительность фильма должна быть положительной.
    @Test
    void test1_addInvalidFilm() {
        //Given
        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> new Random().nextInt(1))
                .limit(201).forEach(x -> sb.append("a"));
        Film filmBlankName = new Film(1, " ", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmEmptyName = new Film(1, "", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmExtraLongDescription = new Film(1, "film1", sb.toString(),
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmInvalidRelease = new Film(1, "name", "film description",
                FilmValidators.EARLIEST_RELEASE_DATE.minusDays(1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmNegativeDuration = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), -1
                ,new HashSet<>(List.of(2,3)));


        //When
        //Then
        assertAll("Проверка создания invalid фильма",
                () -> assertThrows(ValidationException.class,
                        () -> service.addFilm(filmBlankName),
                        "film with blank name created"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addFilm(filmEmptyName), "film with empty name created"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addFilm(filmExtraLongDescription),
                        "film description length > 200"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addFilm(filmInvalidRelease),
                        "release date before 28-12-1895"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addFilm(filmNegativeDuration),
                        "film duration is negative")
        );
    }

    @Test
    void test2_updateInvalidFilm() {
        //Given
        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> new Random().nextInt(1))
                .limit(201).forEach(x -> sb.append("a"));
        Film filmBlankName = new Film(1, " ", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmEmptyName = new Film(1, "", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmExtraLongDescription = new Film(1, "film1", sb.toString(),
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmInvalidRelease = new Film(1, "name", "film description",
                FilmValidators.EARLIEST_RELEASE_DATE.minusDays(1), 1
                ,new HashSet<>(List.of(2,3)));
        Film filmNegativeDuration = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), -1
                ,new HashSet<>(List.of(2,3)));


        //When
        //Then
        assertAll("Проверка обновления invalid фильма",
                () -> assertThrows(ValidationException.class,
                        () -> service.updateFilm(filmBlankName),
                        "film with blank name updated"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateFilm(filmEmptyName), "film with empty name updated"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateFilm(filmExtraLongDescription),
                        "film description length > 200"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateFilm(filmInvalidRelease),
                        "release date before 28-12-1895"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateFilm(filmNegativeDuration),
                        "film duration is negative")
        );
    }

    @Test
    void test3_likeFilm() throws ValidationException, UserNotFoundException,
            FilmNotFoundException {
        //Given
        Film film = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>());
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        service.addFilm(film);
        service.getUserStorage().addUser(user);

        //When
        service.like(film.getId(), user.getId());
        final Film updFilm = service.findFilm(film.getId());

        //Then
        assertAll("Проверка like фильма",
                () -> assertEquals(1, updFilm.getLikes().size(),
                        "Неверное кол-во лайков"),
                () -> assertEquals(new HashSet<>(List.of(1)), updFilm.getLikes(),
                        "Отличается список лайков")
        );
    }

    @Test
    void test4_disLikeFilm() throws ValidationException, FilmNotFoundException,
            UserNotFoundException {
        //Given
        Film film = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(1,2,3)));
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        service.addFilm(film);
        service.getUserStorage().addUser(user);

        //When
        service.disLike(film.getId(), user.getId());
        final Film updFilm = service.findFilm(film.getId());

        //Then
        assertAll("Проверка disLike фильма",
                () -> assertEquals(2, updFilm.getLikes().size(),
                        "Неверное кол-во лайков после удаления"),
                () -> assertEquals(new HashSet<>(List.of(2,3)), film.getLikes(),
                        "Отличается список лайков после удаления")
        );
    }

    @Test
    void test5_getNMostPopularFilms() throws ValidationException {
        //Given
        add11Films(service);

        //When
        final Collection<Film> mostPopularFilms = service.findNMostPopularFilms(
                Optional.of(11)); // 11 фильмов
        final Collection<Film> nullMostPopularFilms = service.findNMostPopularFilms(
                Optional.empty()); // 10 фильмов т.к. параметр count = null

        //Then
        Collection<Film> expectedFilms = service.findAllFilms().stream()
                        .sorted(this::compare).limit(11)
                        .collect(Collectors.toList());
        Collection<Film> expectedFilmsWithNull = service.findAllFilms().stream()
                .sorted(this::compare).limit(10)
                .collect(Collectors.toList());
        assertAll("Проверка списка N наиболее популярных фильма",
                () -> assertEquals(11, mostPopularFilms.size(),
                        "Неверное кол-во фильмов"),
                () -> assertEquals(expectedFilms, mostPopularFilms,
                        "Отличается список фильмов"),
                () -> assertEquals(10, nullMostPopularFilms.size(),
                        "Неверное кол-во фильмов (count == empty)"),
                () -> assertEquals(expectedFilmsWithNull, nullMostPopularFilms,
                        "Отличается список фильмов (count == empty)")
        );
    }

    static void add11Films(FilmService service) throws ValidationException {
        List<Integer> likes = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            int id = i + 1;
            Film film = new Film(id, "film" + id,
                    "film description" + id,
                    LocalDate.of(2017, 1, 1),
                    id, new HashSet<>());
            if (i > 0) {
                likes.add(id);
                film.setLikes(new HashSet<>(likes));
            }

            service.addFilm(film);
        }
    }

    int compare(Film o1, Film o2) {
        return Integer.compare(o2.getLikes().size(), o1.getLikes().size());
    }
}