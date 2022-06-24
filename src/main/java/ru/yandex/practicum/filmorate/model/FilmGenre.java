package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilmGenre {
    private final int id;
    private String name;

    @JsonCreator
    public FilmGenre(int id) {
        this.id = id;
    }
}
