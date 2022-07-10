package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    Collection<User> findAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User findUser(Integer id);

    Collection<User> findFriends(Integer id);

    Collection<User> findCommonFriends(Integer id, Integer otherId);

    void addFriend(Integer id, Integer otherId);

    void deleteFriend(Integer id, Integer otherId);

    void deleteUser(int id);

}
