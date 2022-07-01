package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private final Map<Integer, User> users;
    private int userUniqueId;


    public InMemoryUserStorage() {
        this.users = new HashMap<>();
        this.userUniqueId = 1;
    }

    @Override
    public Collection<User> findAllUsers() {
        log.info("Количество пользователей - {}", users.size());
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(userUniqueId++);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь - {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Обновлена информация о пользователе - {}", user);
        return user;
    }

    @Override
    public User findUser(Integer id) {
        return users.get(id);
    }

    @Override
    public Collection<User> findFriends(Integer id) {
        final Set<Integer> friendIds = findUser(id).getFriends();
        return findAllUsers().stream()
                .filter(x -> friendIds.contains(x.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriends(Integer id, Integer otherId) {
        final User user = findUser(id);
        final User otherUser = findUser(otherId);
        Set<Integer> commonIds = new HashSet<>(user.getFriends());
        commonIds.addAll(otherUser.getFriends());

        return findAllUsers().stream()
                .filter(x -> commonIds.contains(x.getId()))
                .filter(x -> !(x.getId() == id
                        || x.getId() == otherId))
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(Integer id, Integer otherId) {
        final User user = findUser(id);
        user.addFriend(otherId);
        updateUser(user);
    }

    @Override
    public void deleteFriend(Integer id, Integer otherId) {
        final User user = findUser(id);
        user.removeFriend(otherId);
        updateUser(user);
    }

}
