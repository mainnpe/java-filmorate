package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller;

    @BeforeEach
    void beforeEach() {
        controller = new UserController();
    }

    @Test
    void test1_addValidUser() {
        //Given
        User user = new User(1, "email@email.com",
                "login", "name", LocalDate.of(2000,1,1));
        //When
        User savedUser = controller.addUser(user);

        //Then
        assertAll("Проверка реквизитов пользователя",
                () -> assertEquals(user.getId(), savedUser.getId(), "поля id не равны"),
                () -> assertEquals(user.getEmail(), savedUser.getEmail(), "поля email не равны"),
                () -> assertEquals(user.getLogin(), savedUser.getLogin(), "поля login не равны"),
                () -> assertEquals(user.getName(), savedUser.getName(), "поля name не равны"),
                () -> assertEquals(user.getBirthday(), savedUser.getBirthday(),
                        "поля birthday не равны")
        );
    }

    @Test
    void test2_updateValidUser() {
        //Given
        User user = new User(1, "email@email.com",
                "login", "name", LocalDate.of(2000,1,1));
        controller.addUser(user);

        //When
        User updUser = new User(1, "newemail@email.com",
                "newLogin", "newName", LocalDate.of(2001,2,15));
        User updatedUser = controller.updateUser(updUser);

        //Then
        assertAll("Проверка реквизитов пользователя",
                () -> assertEquals(updUser.getId(), updatedUser.getId(), "поля id не равны"),
                () -> assertEquals(updUser.getEmail(), updatedUser.getEmail(), "поля email не равны"),
                () -> assertEquals(updUser.getLogin(), updatedUser.getLogin(), "поля login не равны"),
                () -> assertEquals(updUser.getName(), updatedUser.getName(), "поля name не равны"),
                () -> assertEquals(updUser.getBirthday(), updatedUser.getBirthday(),
                        "поля birthday не равны")
        );
    }

//    электронная почта не может быть пустой и должна содержать символ @;
//    логин не может быть пустым и содержать пробелы;
//    имя для отображения может быть пустым — в таком случае будет использован логин;
//    дата рождения не может быть в будущем.
    @Test
    void test3_addInvalidUser() {
        //Given
        User userEmailBlank = new User(1, " ",
                "login", "name", LocalDate.of(2000,1,1));
        User userEmailEmpty = new User(1, "",
                "login", "name", LocalDate.of(2000,1,1));
        User userLoginEmpty = new User(1, "email@email.com",
                "", "name", LocalDate.of(2000,1,1));
        User userLoginBlank = new User(1, "email@email.com",
                " ", "name", LocalDate.of(2000,1,1));
        User userFutureBirthDate = new User(1, "email@email.com",
                "login", "name", LocalDate.now().plusDays(1));

        //When
        //Then
        assertAll("Проверка создания invalid пользователя",
                () -> assertThrows(ValidationException.class,
                        () -> controller.addUser(userEmailBlank),
                        "user with blank email created"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addUser(userEmailEmpty), "user with empty email created"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addUser(userLoginBlank), "user with blank login created"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addUser(userLoginEmpty), "user with empty login created"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.addUser(userFutureBirthDate), "user with " +
                                "future birthday date created") 
        );
    }

    @Test
    void test4_updateInvalidUser() {
        //Given
        User userEmailBlank = new User(1, " ",
                "login", "name", LocalDate.of(2000,1,1));
        User userEmailEmpty = new User(1, "",
                "login", "name", LocalDate.of(2000,1,1));
        User userLoginEmpty = new User(1, "email@email.com",
                "", "name", LocalDate.of(2000,1,1));
        User userLoginBlank = new User(1, "email@email.com",
                " ", "name", LocalDate.of(2000,1,1));
        User userFutureBirthDate = new User(1, "email@email.com",
                "login", "name", LocalDate.now().plusDays(1));

        //When
        //Then
        assertAll("Проверка обновления пользователя с invalid параметрами",
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateUser(userEmailBlank),
                        "user with blank email updated"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateUser(userEmailEmpty), "user with empty email updated"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateUser(userLoginBlank), "user with blank login updated"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateUser(userLoginEmpty), "user with empty login updated"),
                () -> assertThrows(ValidationException.class,
                        () -> controller.updateUser(userFutureBirthDate), "user with " +
                                "future birthday date updated")
        );
    }

    @Test
    void test5_nullTest() {
        assertAll("Проверка на NonNull поля USER",
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, null, "login",
                                "name", LocalDate.of(2000,1,1))),
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, "email@email", null, "name",
                                LocalDate.of(2000,1,1))),
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, "email@email", "login", "" +
                                "name", null))
        );

    }

    @Test
    void test6_hashAndEqualTest() {
        Set<User> users = new HashSet<>();
        User user = new User(1, "email@email.com",
                "login", "name", LocalDate.of(2000,1,1));
        User user2 = new User(1, "newemail@email.com",
                "newlogin", "name", LocalDate.of(2000,1,1));
        users.add(user);
        users.add(user2);
        System.out.println(users);
        assertEquals(user, user2);
        assertEquals(1, users.size());

    }


}