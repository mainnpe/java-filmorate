package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class Rate {
    @NonNull
    @Positive
    int user_id;
    @NonNull
    @Positive
    int film_id;
    @Min(1)
    @Max(10)
    float rate;
}
