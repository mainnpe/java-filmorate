package ru.yandex.practicum.filmorate.service.validator;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

@Component
public class UserValidators {
    public boolean validateFormat(User user) {
        boolean emailCheck = !user.getEmail().isBlank() && // электронная почта не может быть пустой
                user.getEmail().contains("@"); // и должна содержать символ @;
        boolean loginCheck = !(user.getLogin().isBlank() || //логин не может быть пустым
                user.getLogin().contains(" ")); //и содержать пробелы;
        boolean birthdayCheck = user.getBirthday().isBefore(LocalDate.now()); // дата рождения
        // не может быть в будущем.
        return emailCheck && loginCheck && birthdayCheck;
    }

    public void isExists(UserStorage storage, Integer id,
                                String message, Logger log) throws UserNotFoundException {
        if (storage.findUser(id) == null) {
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }
}
