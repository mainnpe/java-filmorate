package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Include private int id;
    @NonNull private String email;
    @NonNull private String login;
    private String name;
    @NonNull private LocalDate birthday;

//    целочисленный идентификатор — id;
//    электронная почта — email;
//    логин пользователя — login;
//    имя для отображения — name;
//    дата рождения — birthday.

}
