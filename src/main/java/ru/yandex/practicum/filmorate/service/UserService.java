package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.validator.UserValidators;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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
        /*if(userStorage.findUser(user.getId()) == null) {
            log.warn("Пользователь {} не существует", user);
            throw new UserNotFoundException("Ошибка при обновлении информации " +
                    "о пользователе.");
        }*/
        return userStorage.updateUser(user);
    }

    public void addFriend(Integer id, Integer otherId) throws UserNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, id, "Невалидный id пользователя, " +
                "направившего заявку на добавление в друзья.", log);
        UserValidators.isExists(userStorage, otherId,
                String.format("Ошибка при добавлении в друзья. Пользователь с id = %s не существует."
                        , otherId), log);

        //Добавляем пользователя otherUser в друзья User
        final User user = userStorage.findUser(id);
        user.addFriend(otherId);
        userStorage.updateUser(user);
        //Добавляем пользователя User в друзья otherUser
        final User otherUser = userStorage.findUser(otherId);
        otherUser.addFriend(id);
        userStorage.updateUser(otherUser);
    }

    public void deleteFriend(Integer id, Integer otherId) throws UserNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, id,
                "Невалидный id пользователя, направившего заявку " +
                        "на удаление из друзей.", log);
        UserValidators.isExists(userStorage, otherId,
                String.format("Ошибка при удалении. Пользователь с id = %s не существует."
                        , otherId), log);

        //Удаляем пользователя otherUser из друзей User
        final User user = userStorage.findUser(id);
        user.removeFriend(otherId);
        userStorage.updateUser(user);
        //Удаляем пользователя User из друзей otherUser
        final User otherUser = userStorage.findUser(otherId);
        otherUser.removeFriend(id);
        userStorage.updateUser(otherUser);
    }

    public Collection<User> findFriends(Integer id) throws UserNotFoundException {
        // Проверить существование User
        UserValidators.isExists(userStorage, id,
                "Пользователь с id = %s не существует.", log);

        //Определить список друзей
        final Set<Integer> friendIds = userStorage.findUser(id).getFriends();
        return userStorage.findAllUsers().stream()
                .filter(x -> friendIds.contains(x.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Integer id, Integer otherId) throws UserNotFoundException {
        // Проверить существование обоих User
        UserValidators.isExists(userStorage, id, String.format(
                "Пользователь с id = %s не существует.", otherId), log);
        UserValidators.isExists(userStorage, otherId, String.format(
                "Пользователь с id = %s не существует.", otherId), log);

        //Определить список id общих друзей
        final User user = userStorage.findUser(id);
        final User otherUser = userStorage.findUser(otherId);
        Set<Integer> userIds = new HashSet<>(user.getFriends());
        userIds.addAll(otherUser.getFriends());

        return userStorage.findAllUsers().stream()
                .filter(x -> userIds.contains(x.getId()))
                .filter(x -> !(x.getId() == id
                        || x.getId() == otherId))
                .collect(Collectors.toList());
    }

}
