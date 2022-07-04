package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director {

    private final int id;
    private String name;

    @JsonCreator
    public Director(int id) {
        this.id = id;
    }
}
