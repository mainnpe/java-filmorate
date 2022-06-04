package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserStorageTest {
    UserStorage storage;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryUserStorage();
    }

    @Test
    void test1_addValidUser() throws ValidationException {
        //Given
        User user = new User(1, "email@email.com",
                "login", "name", LocalDate.of(2000,1,1)
                ,new HashSet<Integer>(List.of(1,2,3)));
        //When
        User savedUser = storage.addUser(user);

        //Then
        assertAll("Проверка реквизитов пользователя",
                () -> assertEquals(user.getId(), savedUser.getId(), "поля id не равны"),
                () -> assertEquals(user.getEmail(), savedUser.getEmail(), "поля email не равны"),
                () -> assertEquals(user.getLogin(), savedUser.getLogin(), "поля login не равны"),
                () -> assertEquals(user.getName(), savedUser.getName(), "поля name не равны"),
                () -> assertEquals(user.getBirthday(), savedUser.getBirthday(),
                        "поля birthday не равны"),
                () -> assertEquals(user.getFriends(), savedUser.getFriends(),
                        "поля friends не равны")
        );
    }

    @Test
    void test2_updateValidUser() throws ValidationException {
        //Given
        User user = new User(1, "email@email.com",
                "login", "name", LocalDate.of(2000,1,1)
                ,new HashSet<Integer>(List.of(1,2,3)));
        storage.addUser(user);

        //When
        User updUser = new User(1, "newemail@email.com",
                "newLogin", "newName", LocalDate.of(2001,2,15)
                ,new HashSet<Integer>(List.of(3,1,2)));
        User updatedUser = storage.updateUser(updUser);

        //Then
        assertAll("Проверка реквизитов пользователя",
                () -> assertEquals(updUser.getId(), updatedUser.getId(), "поля id не равны"),
                () -> assertEquals(updUser.getEmail(), updatedUser.getEmail(), "поля email не равны"),
                () -> assertEquals(updUser.getLogin(), updatedUser.getLogin(), "поля login не равны"),
                () -> assertEquals(updUser.getName(), updatedUser.getName(), "поля name не равны"),
                () -> assertEquals(updUser.getBirthday(), updatedUser.getBirthday(),
                        "поля birthday не равны"),
                () -> assertEquals(updUser.getFriends(), updatedUser.getFriends(),
                        "поля friends не равны")
        );
    }



    @Test
    void test5_nullTest() {
        assertAll("Проверка на NonNull поля USER",
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, null, "login",
                                "name", LocalDate.of(2000,1,1)
                                , new HashSet<>())),
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, "email@email", null, "name",
                                LocalDate.of(2000,1,1), new HashSet<>())),
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, "email@email", "login", "" +
                                "name", null, new HashSet<>()))
        );

    }

    @Test
    void test6_hashAndEqualTest() {
        Set<User> users = new HashSet<>();
        User user = new User(1, "email@email.com",
                "login", "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        User user2 = new User(1, "newemail@email.com",
                "newlogin", "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        users.add(user);
        users.add(user2);
        System.out.println(users);
        assertEquals(user, user2);
        assertEquals(1, users.size());

    }

}