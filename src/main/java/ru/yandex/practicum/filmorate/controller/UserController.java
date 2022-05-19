package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int userUniqueId = 1;


    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Количество пользователей - {}", users.size());
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        if (!validate(user)) {
            log.warn("Ошибка при создании пользователя");
            throw new ValidationException("Ошибка при создании пользователя");
        }
        user.setId(userUniqueId++);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь - {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        if (!validate(user)) {
            log.warn("Ошибка при обновлении пользователя");
            throw new ValidationException("Ошибка при обновлении информации " +
                    "о пользователе.");
        }
        if(!users.containsKey(user.getId())) {
            log.warn("Пользователь {} не существует", user);
            throw new ValidationException("Ошибка при обновлении информации " +
                    "о пользователе.");
        }
        users.put(user.getId(), user);
        log.info("Обновлена информация о пользователе - {}", user);
        return user;
    }

    public static boolean validate(User user) {
        boolean emailCheck = !user.getEmail().isBlank() && // электронная почта не может быть пустой
                user.getEmail().contains("@"); // и должна содержать символ @;
        boolean loginCheck = !(user.getLogin().isBlank() || //логин не может быть пустым
                user.getLogin().contains(" ")); //и содержать пробелы;
        boolean birthdayCheck = user.getBirthday().isBefore(LocalDate.now()); // дата рождения
                                                                    // не может быть в будущем.
        return emailCheck && loginCheck && birthdayCheck;
    }

}
