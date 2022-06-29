package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEventType;
import ru.yandex.practicum.filmorate.model.eventmanager.UserOperation;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    private final EventManager eventManager;

    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User findUser(Integer id) throws UserNotFoundException {
        UserValidators.isExists(userStorage, id, String.format(
                    "Пользователь с id = %s не существует.", id), log);

        return userStorage.findUser(id);
    }


    public User addUser(User user) throws ValidationException {
        if (!UserValidators.validateFormat(user)) {
            log.warn("Ошибка при создании пользователя");
            throw new ValidationException("Ошибка при создании пользователя");
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException, UserNotFoundException {
        if (!UserValidators.validateFormat(user)) {
            log.warn("Ошибка при обновлении пользователя");
            throw new ValidationException("Ошибка при обновлении информации " +
                    "о пользователе.");
        }
        UserValidators.isExists(userStorage, user.getId(), String.format(
                "Пользователь с id = %s не существует.", user.getId()), log);

        return userStorage.updateUser(user);
    }

    public void addFriend(Integer id, Integer otherId) throws UserNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, id, "Невалидный id пользователя, " +
                "направившего заявку на добавление в друзья.", log);
        UserValidators.isExists(userStorage, otherId,
                String.format("Ошибка при добавлении в друзья. Пользователь с id = %s не существует."
                        , otherId), log);

        userStorage.addFriend(id, otherId);

        eventManager.register(new UserEvent(
                id,
                otherId,
                UserEventType.FRIEND,
                UserOperation.ADD
        ));
    }

    public void deleteFriend(Integer id, Integer otherId) throws UserNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, id,
                "Невалидный id пользователя, направившего заявку " +
                        "на удаление из друзей.", log);
        UserValidators.isExists(userStorage, otherId,
                String.format("Ошибка при удалении. Пользователь с id = %s не существует."
                        , otherId), log);

        userStorage.deleteFriend(id, otherId);

        eventManager.register(new UserEvent(
                id,
                otherId,
                UserEventType.FRIEND,
                UserOperation.REMOVE
        ));
    }

    public Collection<User> findFriends(Integer id) throws UserNotFoundException {
        // Проверить существование User
        UserValidators.isExists(userStorage, id,
                "Пользователь с id = %s не существует.", log);

        //Определить список друзей
        return userStorage.findFriends(id);
    }

    public Collection<User> findCommonFriends(Integer id, Integer otherId) throws UserNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, id, String.format(
                "Пользователь с id = %s не существует.", otherId), log);
        UserValidators.isExists(userStorage, otherId, String.format(
                "Пользователь с id = %s не существует.", otherId), log);

        //Определить список id общих друзей
        return userStorage.findCommonFriends(id, otherId);
    }

}
