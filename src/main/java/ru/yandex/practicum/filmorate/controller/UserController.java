package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.interfaces.EventService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EventService eventManager;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable Integer id) throws UserNotFoundException {
        return userService.findUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findUserFriends(@PathVariable Integer id)
            throws UserNotFoundException {
        return userService.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable Integer id,
                                              @PathVariable Integer otherId)
            throws UserNotFoundException
    {
        return userService.findCommonFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, UserNotFoundException {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id,
                          @PathVariable Integer friendId)
            throws UserNotFoundException
    {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id,
                             @PathVariable Integer friendId)
            throws UserNotFoundException
    {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable Integer id)
            throws UserNotFoundException {
        return userService.getRecommendations(id);
    }

    @GetMapping("{id}/feed")
    public ResponseEntity<Collection<UserEvent>> getUserEvents(
            @PathVariable int id){
        return ResponseEntity.ok(eventManager.getUserEventsById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id)
            throws UserNotFoundException
    {
        userService.deleteUser(id);
    }

}
