package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Film {
    @EqualsAndHashCode.Include private int id;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private LocalDate releaseDate;
    @NonNull private Integer duration;
    private Set<Integer> likes;
    @NonNull private MPARating mpa;
    private Set<FilmGenre> genres;


    @JsonCreator
    public Film(@NonNull String name, @NonNull String description,
                @NonNull LocalDate releaseDate, @NonNull Integer duration,
                @NonNull MPARating mpa, Set<FilmGenre> genres)
    {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.mpa = mpa;
//        if (genres == null) {
//            this.genres = new HashSet<>();
//        }
        this.genres = genres;

    }



    //    целочисленный идентификатор — id;
//    название — name;
//    описание — description;
//    дата релиза — releaseDate;
//    продолжительность фильма — duration.


    public void like(Integer id) {
        likes.add(id);
    }

    public void disLike(Integer id) {
        likes.remove(id);
    }
}
