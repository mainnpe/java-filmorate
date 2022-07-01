package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@ToString
public class Review {

    private int id;

    private String content;

    @JsonProperty("isPositive")
    @Getter(AccessLevel.NONE)
    private Boolean positive;

    private Integer userId;

    private Integer filmId;

    private Integer useful;

    public Boolean isPositive() {
        return positive;
    }
}
