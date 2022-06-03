package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmStorageTest {
    FilmStorage storage;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryFilmStorage();
    }

    @Test
    void test1_addValidFilm() throws ValidationException {
        //Given
        Film film = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        //When
        Film savedFilm = storage.addFilm(film);

        //Then
        assertAll("Проверка фильма при создании",
                () -> assertEquals(film.getId(), savedFilm.getId(), "поля id не равны"),
                () -> assertEquals(film.getName(), savedFilm.getName(), "поля name не равны"),
                () -> assertEquals(film.getDescription(), savedFilm.getDescription(),
                        "поля description не равны"),
                () -> assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate(),
                        "поля releaseDate не равны"),
                () -> assertEquals(film.getDuration(), savedFilm.getDuration(),
                        "поля duration не равны"),
                () -> assertEquals(film.getLikes(), savedFilm.getLikes(),
                        "поля likes не равны")
        );
    }

    @Test
    void test2_updateValidFilm() throws ValidationException {
        //Given
        Film film = new Film(1, "film1", "film description",
                LocalDate.of(2017, 1, 1), 1
                ,new HashSet<>(List.of(2,3)));
        storage.addFilm(film);
        //When
        Film updFilm = new Film(1, "new_film1", "new film description",
                LocalDate.of(2017, 2, 1), 2
                ,new HashSet<>(List.of(4,5)));
        Film savedFilm = storage.updateFilm(updFilm);

        //Then
        assertAll("Проверка фильма при обновлении",
                () -> assertEquals(updFilm.getId(), savedFilm.getId(), "поля id не равны"),
                () -> assertEquals(updFilm.getName(), savedFilm.getName(), "поля name не равны"),
                () -> assertEquals(updFilm.getDescription(), savedFilm.getDescription(),
                        "поля description не равны"),
                () -> assertEquals(updFilm.getReleaseDate(), savedFilm.getReleaseDate(),
                        "поля releaseDate не равны"),
                () -> assertEquals(updFilm.getDuration(), savedFilm.getDuration(),
                        "поля duration не равны"),
                () -> assertEquals(updFilm.getLikes(), savedFilm.getLikes(),
                        "поля likes не равны")
        );
    }

}