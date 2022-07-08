package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

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
    public Collection<Film> findFilms(List<Integer> ids) {
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(filmUniqueId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм - {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film){
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
                    .sorted((o1,o2) -> Integer.compare(o2.getLikes().size(),
                                o1.getLikes().size())
                    ).limit(count.orElse(10))
                    .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<Integer>> getAllFilmsLikes() {
        return null;
    }
    
    @Override
    public Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        return null;
    }
    
    @Override
    public Collection<Film> findCommonFilmsByUsersIds(int userId, int friendId) {

        return null;
    }

    @Override
    public void updateRate(int id, float rate) {

    }

}
