package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MPARating {
    private final int id;
    private String name;

    @JsonCreator
    public MPARating(int id) {
        this.id = id;
    }
}
