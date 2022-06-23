package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MPARatingDao;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmStorageTest {
    private final FilmStorage storage;
    private final MPARatingDao mpaRatingDao;
    private final FilmGenreDao filmGenreDao;
    private final UserStorage userStorage;

    @Test
    void test1_addFilm() {
        //Given
        MPARating mpa = mpaRatingDao.findRating(1);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findAllGenres());

        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, mpa, genres);


        //When
        Film savedFilm = storage.addFilm(film);

        //Then
        assertAll("Check film add",
                () -> assertEquals(1, savedFilm.getId(), "id field incorrect"),
                () -> assertEquals(film.getName(), savedFilm.getName(), "name field incorrect"),
                () -> assertEquals(film.getDescription(), savedFilm.getDescription(),
                        "description field incorrect"),
                () -> assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate(),
                        "releaseDate field incorrect"),
                () -> assertEquals(film.getDuration(), savedFilm.getDuration(),
                        "duration field incorrect"),
                () -> assertEquals(film.getMpa(), savedFilm.getMpa(),
                        "MPA field incorrect"),
                () -> assertEquals(film.getGenres(), savedFilm.getGenres(),
                        "genres field incorrect")
        );
    }

    @Test
    void test2_updateFilm() {
        //Given
        MPARating mpa = mpaRatingDao.findRating(1);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findAllGenres());

        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, mpa, genres);
        Film savedFilm = storage.addFilm(film);

        //When
        MPARating updMpa = mpaRatingDao.findRating(3);//upd mpa
        Set<FilmGenre> updGenres = new HashSet<>(genres);
        updGenres.remove(filmGenreDao.findGenre(2)); //upd genres
        Film updFilm = new Film( "UPDfilm1", "UPDfilm description",
                LocalDate.of(1925, Month.JULY, 13),
                115, updMpa, updGenres);
        updFilm.setId(savedFilm.getId());
        
        Film updatedFilm = storage.updateFilm(updFilm);

        //Then
        assertAll("Check film update",
                () -> assertEquals(1, updatedFilm.getId(), "id field incorrect"),
                () -> assertEquals(updFilm.getName(), updatedFilm.getName(), "name field incorrect"),
                () -> assertEquals(updFilm.getDescription(), updatedFilm.getDescription(),
                        "description field incorrect"),
                () -> assertEquals(updFilm.getReleaseDate(), updatedFilm.getReleaseDate(),
                        "releaseDate field incorrect"),
                () -> assertEquals(updFilm.getDuration(), updatedFilm.getDuration(),
                        "duration field incorrect"),
                () -> assertEquals(updFilm.getMpa(), updatedFilm.getMpa(),
                        "MPA field incorrect"),
                () -> assertEquals(updFilm.getGenres(), updatedFilm.getGenres(),
                        "genres field incorrect")
        );
    }

    @Test
    void test3_findFilm() {
        //Given
        MPARating mpa = mpaRatingDao.findRating(1);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findAllGenres());

        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, mpa, genres);

        //When
        storage.addFilm(film);
        Film savedFilm = storage.findFilm(1);

        //Then
        assertAll("Check find film",
                () -> assertEquals(1, savedFilm.getId(), "id field incorrect"),
                () -> assertEquals(film.getName(), savedFilm.getName(), "name field incorrect"),
                () -> assertEquals(film.getDescription(), savedFilm.getDescription(),
                        "description field incorrect"),
                () -> assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate(),
                        "releaseDate field incorrect"),
                () -> assertEquals(film.getDuration(), savedFilm.getDuration(),
                        "duration field incorrect"),
                () -> assertEquals(film.getMpa(), savedFilm.getMpa(),
                        "MPA field incorrect"),
                () -> assertEquals(film.getGenres(), savedFilm.getGenres(),
                        "genres field incorrect")
        );
    }

    @Test
    void test4_add2ndFilmAndFindAllFilms() {
        //Given
        MPARating mpa = mpaRatingDao.findRating(1);
        MPARating mpa2 = mpaRatingDao.findRating(3);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findAllGenres());
        Set<FilmGenre> genres2 = new HashSet<>(genres);
        genres2.remove(filmGenreDao.findGenre(3));
        
        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, mpa, genres);
        Film film2 = new Film( "film2", "film description 2",
                LocalDate.of(1999, Month.JULY, 7),
                162, mpa2, genres2);

        //When
        Film savedFilm1 = storage.addFilm(film);
        Film savedFilm2 = storage.addFilm(film2);
        Collection<Film> films = storage.findAllFilms();

        //Then
        assertAll("Check find all films",
                () -> assertEquals(2, films.size(),
                        "incorrect number of films"),
                () -> assertTrue(films.contains(savedFilm1), "film1 not found"),
                () -> assertTrue(films.contains(savedFilm2), "film2 not found")
        );
        assertAll("Check 2nd film add",
                () -> assertEquals(2, savedFilm2.getId(), "id field incorrect"),
                () -> assertEquals(film2.getName(), savedFilm2.getName(), "name field incorrect"),
                () -> assertEquals(film2.getDescription(), savedFilm2.getDescription(),
                        "description field incorrect"),
                () -> assertEquals(film2.getReleaseDate(), savedFilm2.getReleaseDate(),
                        "releaseDate field incorrect"),
                () -> assertEquals(film2.getDuration(), savedFilm2.getDuration(),
                        "duration field incorrect"),
                () -> assertEquals(film2.getMpa(), savedFilm2.getMpa(),
                        "MPA field incorrect"),
                () -> assertEquals(film2.getGenres(), savedFilm2.getGenres(),
                        "genres field incorrect")
        );
    }

    @Test
    void test5_get2MostPopularFilms() {

        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User user2 = new User("2ndmail@mail.ru","2nddolore",
                "2ndNick Name", LocalDate.of(1956, Month.AUGUST,20));
        MPARating mpa = mpaRatingDao.findRating(1);
        MPARating mpa2 = mpaRatingDao.findRating(3);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findAllGenres());
        Set<FilmGenre> genres2 = new HashSet<>(genres);
        genres2.remove(filmGenreDao.findGenre(3));

        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, mpa, genres);
        Film film2 = new Film( "film2", "film description 2",
                LocalDate.of(1999, Month.JULY, 7),
                162, mpa2, genres2);

        Film savedFilm1 = storage.addFilm(film);
        Film savedFilm2 = storage.addFilm(film2);
        User savedUser1 = userStorage.addUser(user);
        User savedUser2 = userStorage.addUser(user2);

        //When
        // film 2 has 2 likes
        storage.like(savedFilm2.getId(), savedUser1.getId());
        storage.like(savedFilm2.getId(), savedUser2.getId());
        // film 1 have 1 likes
        storage.like(savedFilm1.getId(), savedUser2.getId());

        List<Film> films = (List<Film>) storage.findNMostPopularFilms(Optional.of(2));

        //Then
        assertAll("Check find N most popular films",
                () -> assertEquals(2, films.size(),
                        "incorrect number of films"),
                () -> assertEquals(savedFilm2, films.get(0),
                        "incorrect 1st popular film"),
                () -> assertEquals(savedFilm1, films.get(1),
                        "incorrect 2nd popular film")
        );

    }

    @Test
    void test5_get2MostPopularFilmsUsingDislike() {

        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User user2 = new User("2ndmail@mail.ru","2nddolore",
                "2ndNick Name", LocalDate.of(1956, Month.AUGUST,20));
        MPARating mpa = mpaRatingDao.findRating(1);
        MPARating mpa2 = mpaRatingDao.findRating(3);
        Set<FilmGenre> genres = new HashSet<>(filmGenreDao.findAllGenres());
        Set<FilmGenre> genres2 = new HashSet<>(genres);
        genres2.remove(filmGenreDao.findGenre(3));

        Film film = new Film( "film1", "film description",
                LocalDate.of(1989, Month.JULY, 7),
                100, mpa, genres);
        Film film2 = new Film( "film2", "film description 2",
                LocalDate.of(1999, Month.JULY, 7),
                162, mpa2, genres2);

        Film savedFilm1 = storage.addFilm(film);
        Film savedFilm2 = storage.addFilm(film2);
        User savedUser1 = userStorage.addUser(user);
        User savedUser2 = userStorage.addUser(user2);

        //When
        // film 1 has 0 likes (due to dislike)
        storage.like(savedFilm2.getId(), savedUser1.getId());
        storage.disLike(savedFilm2.getId(), savedUser1.getId());
        // film 1 have 1 likes
        storage.like(savedFilm1.getId(), savedUser2.getId());

        List<Film> films = (List<Film>) storage.findNMostPopularFilms(Optional.of(2));

        //Then
        assertAll("Check find N most popular films",
                () -> assertEquals(2, films.size(),
                        "incorrect number of films"),
                () -> assertEquals(savedFilm1, films.get(0),
                        "incorrect 1st popular film"),
                () -> assertEquals(savedFilm2, films.get(1),
                        "incorrect 2nd popular film")
        );

    }


}