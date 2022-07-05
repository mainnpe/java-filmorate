package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.validator.FilmValidators;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MPARatingDao;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmServiceTest {
    private final FilmService service;

    /*@BeforeEach
    void beforeEach() {
        service = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryUserStorage(),
                new FilmGenreDao(new JdbcTemplate()),
                new MPARatingDao(new JdbcTemplate()),
                new DirectorDao(new JdbcTemplate()),
                new EventManager());
    }*/

    //    Требования:
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
        Film filmBlankName = new Film( " ", "film description",
                LocalDate.of(2017, 1, 1), 1,
                TestConstants.DIRECTOR, TestConstants.MPA, TestConstants.GENRES);
        Film filmEmptyName = new Film( "", "film description",
                LocalDate.of(2017, 1, 1), 1,
                TestConstants.DIRECTOR, TestConstants.MPA, TestConstants.GENRES);
        Film filmExtraLongDescription = new Film( "film1", sb.toString(),
                LocalDate.of(2017, 1, 1), 1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);
        Film filmInvalidRelease = new Film( "name", "film description",
                FilmValidators.EARLIEST_RELEASE_DATE.minusDays(1), 1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);
        Film filmNegativeDuration = new Film( "film1", "film description",
                LocalDate.of(2017, 1, 1), -1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);


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
        Film filmBlankName = new Film( " ", "film description",
                LocalDate.of(2017, 1, 1), 1,
                TestConstants.DIRECTOR, TestConstants.MPA, TestConstants.GENRES);
        Film filmEmptyName = new Film( "", "film description",
                LocalDate.of(2017, 1, 1), 1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);
        Film filmExtraLongDescription = new Film( "film1", sb.toString(),
                LocalDate.of(2017, 1, 1), 1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);
        Film filmInvalidRelease = new Film( "name", "film description",
                FilmValidators.EARLIEST_RELEASE_DATE.minusDays(1), 1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);
        Film filmNegativeDuration = new Film( "film1", "film description",
                LocalDate.of(2017, 1, 1), -1,
                TestConstants.DIRECTOR,TestConstants.MPA, TestConstants.GENRES);


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
        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, TestConstants.DIRECTOR, TestConstants.MPA, TestConstants.GENRES);
        Film film2 = new Film( "film2", "film2 description",
                LocalDate.of(1989, Month.JULY, 7),
                100, TestConstants.DIRECTOR, TestConstants.MPA, TestConstants.GENRES);
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        Film savedFilm = service.addFilm(film);
        Film savedFilm2 = service.addFilm(film2);
        User savedUser = service.getUserStorage().addUser(user);

        //When
        service.like(savedFilm.getId(), savedUser.getId());
        final Collection<Film> likedFilm =
                service.findNMostPopularFilms(Optional.of(1));

        //Then
        assertAll("Проверка like фильма",
                () -> assertEquals(1, likedFilm.size(),
                        "Неверное кол-во лайков"),
                () -> assertTrue(likedFilm.contains(savedFilm),
                        "Отличается список лайков")
        );
    }

    @Test
    /*void test4_disLikeFilm() throws ValidationException, FilmNotFoundException,
            UserNotFoundException {
        //Given
        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, TestConstants.DIRECTOR, TestConstants.MPA, TestConstants.GENRES);
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        film.setLikes(new HashSet<>(List.of(1,2,3)));
        Film savedFilm = service.addFilm(film);
        User savedUser = service.getUserStorage().addUser(user);

        //When
        service.disLike(savedFilm.getId(), savedUser.getId());
        final Film updFilm = service.findFilm(film.getId());

        //Then
        assertAll("Проверка disLike фильма",
                () -> assertEquals(2, updFilm.getLikes().size(),
                        "Неверное кол-во лайков после удаления"),
                () -> assertEquals(new HashSet<>(List.of(2,3)), updFilm.getLikes(),
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
    }*/

    static void add11Films(FilmService service) throws ValidationException {
        List<Integer> likes = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            int id = i + 1;
            Film film = new Film( "film1 "+ id, "film description "+ id,
                    LocalDate.of(1989, Month.JULY, 7), id, TestConstants.DIRECTOR,
                    TestConstants.MPA, TestConstants.GENRES);
            if (i > 0) {
                likes.add(film.getId());
                film.setLikes(new HashSet<>(likes));
            }

            service.addFilm(film);
        }
    }

    int compare(Film o1, Film o2) {
        return Integer.compare(o2.getLikes().size(), o1.getLikes().size());
    }
    private static class TestConstants {
        static Set<Director> DIRECTOR = new HashSet<>();
        static MPARating MPA = new MPARating(1, "G");
        static Set<FilmGenre> GENRES = new HashSet<>(List.of(
                new FilmGenre(1, "Drama"),
                new FilmGenre(2, "Comedy")
        ));
    }
}