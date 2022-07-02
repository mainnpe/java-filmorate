package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;
    private int filmUniqueId;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.filmUniqueId = 1;
    }

    @Override
    public Collection<Film> findAllFilms() {
        log.info("Количество фильмов - {}", films.size());
        return films.values();
    }

    @Override
    public Film findFilm(Integer id) {
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(filmUniqueId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм - {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Обновлена информация о фильме - {}", film);
        return film;
    }

    @Override
    public void like(Integer id, Integer userId) {
        final Film film = findFilm(id);
        film.like(userId);
        updateFilm(film);
    }

    @Override
    public void disLike(Integer id, Integer userId) {
        final Film film = findFilm(id);
        film.disLike(userId);
        updateFilm(film);
    }

    @Override
    public Collection<Film> findNMostPopularFilms(Optional<Integer> count) {
        return findAllFilms().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(),
                        o1.getLikes().size())
                ).limit(count.orElse(10))
                .collect(Collectors.toList());
    }

    public Collection<Director> findAllDirector() {
        ArrayList<Director> director = new ArrayList<>();
        for (Film film : findAllFilms()) {
            for (Director filmDirector : film.getDirectors()) {
                if (!(director.contains(filmDirector))) {
                    director.add(filmDirector);
                }
            }
        }
        return director;
    }

    public String findDirectorById(Integer director_id) {
        for (Director director : findAllDirector()) {
            if(director_id.equals(director.getId())) {
                return director.getName();
            }
        }
        return null;
    }

    @Override
    public Collection<Film> findFilmsOfDirectorSortByLikes(Integer director_id) {
        return findFilmsOfDirector(director_id).stream()
                .sorted(((o1, o2) -> Integer.compare(o2.getLikes().size(),
                        o1.getLikes().size())))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> findFilmsOfDirectorSortByYear(Integer director_id) {
        return findFilmsOfDirector(director_id).stream()
                .sorted(((o1, o2) -> Integer.compare(o2.getReleaseDate().getYear(),
                        o1.getReleaseDate().getYear())))
                .collect(Collectors.toList());
    }

    public Collection<Film> findFilmsOfDirector(Integer director_id) {
        ArrayList<Film> filmsDirector = new ArrayList<>();
        for (Film film : films.values()) {
            for (Director director : film.getDirectors()) {
                if (director_id.equals(director.getId())) {
                    filmsDirector.add(film);
                }
            }
        }
        return filmsDirector;
    }
}
