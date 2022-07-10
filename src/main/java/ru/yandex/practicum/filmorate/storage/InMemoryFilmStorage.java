package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

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
            if (director_id.equals(director.getId())) {
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

    @Override
    public void deleteFilm(int id) {

    }

    @Override
    public Map<Integer, List<Integer>> getAllFilmsLikes() {
        Map<Integer, List<Integer>> filmsLikes = new HashMap<>();
        for (Film film : films.values()) {
            filmsLikes.put(film.getId(), film.getLikes() != null ? List.copyOf(film.getLikes()) : null);
        }
        return filmsLikes;
    }

    @Override
    public Collection<Film> findMostPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        Comparator<Film> filmLikesComparator = Comparator.comparing(Film::getLikes, (s1, s2) -> {
            return s2.size() - s1.size();
        });
        TreeSet<Film> popularFilms = new TreeSet<Film>(filmLikesComparator);
        for (Film film : findAllFilms()) {
            if (film.getReleaseDate().getYear() == year &&
                    film.getGenres().stream().map(FilmGenre::getId).collect(Collectors.toList()).contains(genreId)){
                popularFilms.add(film);
                if (popularFilms.size() > count) {
                    popularFilms.pollLast();
                }
            }
        }
        return popularFilms;
    }

    @Override
    public Collection<Film> findCommonFilmsByUsersIds(int userId, int friendId) {
        List<Film> commonFilms = new ArrayList<>();
        for (Film film : findAllFilms()) {
            if (film.getLikes().contains(userId) && film.getLikes().contains(friendId)){
                commonFilms.add(film);
            }
        }
        return commonFilms;
    }

    @Override
    public Collection<Film> searchByName(String query) {
        List<Film> foundFilms = new ArrayList<>();
        for (Film film : findAllFilms()) {
            if (film.getName().toLowerCase().contains(query.toLowerCase())) {
                foundFilms.add(film);
            }
        }
        return foundFilms;
    }

    @Override
    public Collection<Film> searchByDirector(String query) {
        List<Film> foundFilms = new ArrayList<>();
        for (Film film : findAllFilms()) {
            for (String director : film.getDirectors().stream().map(Director::getName).collect(Collectors.toList())) {
                if (director.toLowerCase().contains(query.toLowerCase())) {
                    foundFilms.add(film);
                    break;
                }
            }
        }
        return foundFilms;
    }

    @Override
    public Collection<Film> searchByNameAndDirector(String query) {
        List<Film> foundFilms = new ArrayList<>();
        for (Film film : findAllFilms()) {
            Film foundFilm = null;
            for (String director : film.getDirectors().stream().map(Director::getName).collect(Collectors.toList())) {
                if (director.toLowerCase().contains(query.toLowerCase())) {
                    foundFilm = film;
                    break;
                }
            }
            if (film.getName().toLowerCase().contains(query.toLowerCase())) {
                foundFilm = film;
            }
            if (foundFilm != null) {
                foundFilms.add(foundFilm);
            }
        }
        return foundFilms;
    }
}
