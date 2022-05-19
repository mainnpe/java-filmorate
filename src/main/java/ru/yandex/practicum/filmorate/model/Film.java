package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.Duration;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Include private int id;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private LocalDate releaseDate;
    @NonNull private Integer duration;


//    целочисленный идентификатор — id;
//    название — name;
//    описание — description;
//    дата релиза — releaseDate;
//    продолжительность фильма — duration.
}
