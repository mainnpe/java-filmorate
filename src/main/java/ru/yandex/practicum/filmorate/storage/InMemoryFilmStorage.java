package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public Film addFilm(Film film) {
        film.setId(filmUniqueId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм - {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if(!films.containsKey(film.getId())) {
            log.warn("Информации о фильме {} не существует", film);
            throw new ValidationException("Ошибка при обновлении информации " +
                    "о фильме.");
        }
        films.put(film.getId(), film);
        log.info("Обновлена информация о фильме - {}", film);
        return film;
    }
}
