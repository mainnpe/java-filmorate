package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @EqualsAndHashCode.Include private int id;
    @NonNull private String email;
    @NonNull private String login;
    private String name;
    @NonNull private LocalDate birthday;
    private Set<Integer> friends;

    @JsonCreator
    public User(@NonNull String email, @NonNull String login,
                String name, @NonNull LocalDate birthday)
    {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }

    //    целочисленный идентификатор — id;
//    электронная почта — email;
//    логин пользователя — login;
//    имя для отображения — name;
//    дата рождения — birthday.
    public void addFriend(Integer id) {
        friends.add(id);
    }
    public void removeFriend(Integer id) {
        friends.remove(id);
    }

}
