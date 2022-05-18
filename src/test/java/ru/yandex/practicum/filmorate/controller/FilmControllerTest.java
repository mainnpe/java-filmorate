package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController controller;

    @BeforeEach
    void beforeEach() {
        controller = new FilmController();
    }

    @Test
    void test1_addValidFilm() {
        //Given
        Film film = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), 1);
        //When
        Film savedFilm = controller.addFilm(film);

        //Then
        assertAll("Проверка фильма при создании",
                () -> assertEquals(film.getId(), savedFilm.getId(), "поля id не равны"),
                () -> assertEquals(film.getName(), savedFilm.getName(), "поля name не равны"),
                () -> assertEquals(film.getDescription(), savedFilm.getDescription(),
                        "поля description не равны"),
                () -> assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate(),
                        "поля releaseDate не равны"),
                () -> assertEquals(film.getDuration(), savedFilm.getDuration(),
                        "поля duration не равны")
        );
    }

    @Test
    void test1_updateValidFilm() {
        //Given
        Film film = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), 1);
        controller.addFilm(film);
        //When
        Film updFilm = new Film(1, "new_film1", "new film description",
                LocalDate.of(2017, 2, 1), 2);
        Film savedFilm = controller.updateFilm(updFilm);

        //Then
        assertAll("Проверка фильма при обновлении",
                () -> assertEquals(updFilm.getId(), savedFilm.getId(), "поля id не равны"),
                () -> assertEquals(updFilm.getName(), savedFilm.getName(), "поля name не равны"),
                () -> assertEquals(updFilm.getDescription(), savedFilm.getDescription(),
                        "поля description не равны"),
                () -> assertEquals(updFilm.getReleaseDate(), savedFilm.getReleaseDate(),
                        "поля releaseDate не равны"),
                () -> assertEquals(updFilm.getDuration(), savedFilm.getDuration(),
                        "поля duration не равны")
        );
    }

    //    название не может быть пустым;
//    максимальная длина описания — 200 символов;
//    дата релиза — не раньше 28 декабря 1895 года;
//    продолжительность фильма должна быть положительной.
    @Test
    void test3_addInvalidFilm() {
        //Given
        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> new Random().nextInt(1))
                .limit(201).forEach(x -> sb.append("a"));
        Film filmBlankName = new Film(1, " ", "film description",
                LocalDate.of(2017, 1, 1), 1);
        Film filmEmptyName = new Film(1, "", "film description",
                LocalDate.of(2017, 1, 1), 1);
        Film filmExtraLongDescription = new Film(1, "film1", sb.toString(),
                LocalDate.of(2017, 1, 1), 1);
        Film filmInvalidRelease = new Film(1, "name", "film description",
                FilmController.EARLIEST_RELEASE_DATE.minusDays(1), 1);
        Film filmNegativeDuration = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), -1);


        //When
        //Then
        assertAll("Проверка создания invalid фильма",
                () -> assertThrows(ValidationException.class,
                        () -> controller.addFilm(filmBlankName),
                        "film with blank name created"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addFilm(filmEmptyName), "film with empty name created"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addFilm(filmExtraLongDescription),
                        "film description length > 200"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addFilm(filmInvalidRelease), 
                        "release date before 28-12-1895"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addFilm(filmNegativeDuration), 
                        "film duration is negative")
        );
    }

    @Test
    void test4_updateInvalidFilm() {
        //Given
        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> new Random().nextInt(1))
                .limit(201).forEach(x -> sb.append("a"));
        Film filmBlankName = new Film(1, " ", "film description",
                LocalDate.of(2017, 1, 1), 1);
        Film filmEmptyName = new Film(1, "", "film description",
                LocalDate.of(2017, 1, 1), 1);
        Film filmExtraLongDescription = new Film(1, "film1", sb.toString(),
                LocalDate.of(2017, 1, 1), 1);
        Film filmInvalidRelease = new Film(1, "name", "film description",
                FilmController.EARLIEST_RELEASE_DATE.minusDays(1), 1);
        Film filmNegativeDuration = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), -1);


        //When
        //Then
        assertAll("Проверка обновления invalid фильма",
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateFilm(filmBlankName),
                        "film with blank name updated"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateFilm(filmEmptyName), "film with empty name updated"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateFilm(filmExtraLongDescription),
                        "film description length > 200"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateFilm(filmInvalidRelease),
                        "release date before 28-12-1895"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateFilm(filmNegativeDuration),
                        "film duration is negative")
        );
    }


}