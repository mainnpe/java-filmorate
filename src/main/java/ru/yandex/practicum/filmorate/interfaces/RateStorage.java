package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Rate;

public interface RateStorage {
    boolean findByFilmAndUserId(int id, int filmId);
    void removeRate(Integer id, Integer userId);

    void addRate(Integer id, Integer userId, Float rate);
}
