package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    UserService service;

    @BeforeEach
    void beforeEach() {
        service = new UserService(
                new InMemoryUserStorage()
        );
    }

    //  Требования:
    //    электронная почта не может быть пустой и должна содержать символ @;
    //    логин не может быть пустым и содержать пробелы;
    //    имя для отображения может быть пустым — в таком случае будет использован логин;
    //    дата рождения не может быть в будущем.
    @Test
    void test1_addInvalidUser() {
        //Given
        Set<Integer> friends = new HashSet<>();
        User userEmailBlank = new User(1, " ",
                "login", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userEmailEmpty = new User(1, "",
                "login", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userLoginEmpty = new User(1, "email@email.com",
                "", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userLoginBlank = new User(1, "email@email.com",
                " ", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userFutureBirthDate = new User(1, "email@email.com",
                "login", "name", LocalDate.now().plusDays(1)
                ,friends);

        //When
        //Then
        assertAll("Проверка создания invalid пользователя",
                () -> assertThrows(ValidationException.class,
                        () -> service.addUser(userEmailBlank),
                        "user with blank email created"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addUser(userEmailEmpty), "user with empty email created"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addUser(userLoginBlank), "user with blank login created"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addUser(userLoginEmpty), "user with empty login created"),
                () -> assertThrows(ValidationException.class,
                        () -> service.addUser(userFutureBirthDate), "user with " +
                                "future birthday date created")
        );
    }

    @Test
    void test2_updateInvalidUser() {
        //Given
        Set<Integer> friends = new HashSet<>();
        User userEmailBlank = new User(1, " ",
                "login", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userEmailEmpty = new User(1, "",
                "login", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userLoginEmpty = new User(1, "email@email.com",
                "", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userLoginBlank = new User(1, "email@email.com",
                " ", "name", LocalDate.of(2000,1,1)
                ,friends);
        User userFutureBirthDate = new User(1, "email@email.com",
                "login", "name", LocalDate.now().plusDays(1)
                ,friends);

        //When
        //Then
        assertAll("Проверка обновления пользователя с invalid параметрами",
                () -> assertThrows(ValidationException.class,
                        () -> service.updateUser(userEmailBlank),
                        "user with blank email updated"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateUser(userEmailEmpty), "user with empty email updated"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateUser(userLoginBlank), "user with blank login updated"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateUser(userLoginEmpty), "user with empty login updated"),
                () -> assertThrows(ValidationException.class,
                        () -> service.updateUser(userFutureBirthDate), "user with " +
                                "future birthday date updated")
        );
    }

    @Test
    void test3_addFriendsValid() throws ValidationException, UserNotFoundException {
        //Given
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        User otherUser = new User(2, "email2@email.com", "login2",
                "name2", LocalDate.of(2000,2,1)
                , new HashSet<>());
        service.addUser(user);
        service.addUser(otherUser);

        //When
        service.addFriend(user.getId(), otherUser.getId());
        final Collection<User> userFriends = service.findFriends(user.getId());
        final Collection<User> otherUserFriends = service.findFriends(otherUser.getId());

        //Then
        List<User> expUserFriends = List.of(otherUser);
        List<User> expOtherUserFriends = List.of();
        assertAll("Проверка списка друзей",
                () -> assertEquals(expUserFriends, userFriends,
                        String.format("Неверный список друзей пользователя %s", user)),
                () -> assertEquals(expOtherUserFriends, otherUserFriends,
                        String.format("Неверный список друзей пользователя %s", otherUser))
        );
    }

    @Test
    void test4_addFriendsInvalidIds() throws ValidationException {
        //Given
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        User otherUser = new User(2, "email2@email.com", "login2",
                "name2", LocalDate.of(2000,2,1)
                , new HashSet<>());
        service.addUser(user);
        service.addUser(otherUser);

        //When

        //Then
        assertAll("Проверка добавления в друзья пользователя с invalid параметрами",
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.addFriend(user.getId(), 3),
                        "пользователя otherUser не существует"),
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.addFriend(4, otherUser.getId()),
                        "пользователя User не существует"),
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.addFriend(user.getId(), null),
                        "пользователя otherUser не существует"),
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.addFriend(null, otherUser.getId()),
                        "пользователя User не существует")

        );
    }

    @Test
    void test5_removeFriendsValid() throws ValidationException, UserNotFoundException {
        //Given
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        User otherUser = new User(2, "email2@email.com", "login2",
                "name2", LocalDate.of(2000,2,1)
                , new HashSet<>());
        user.addFriend(2);
        otherUser.addFriend(1);
        service.addUser(user);
        service.addUser(otherUser);

        //When
        service.deleteFriend(user.getId(), otherUser.getId());
        final Collection<User> userFriends = service.findFriends(user.getId());
        final Collection<User> otherUserFriends = service.findFriends(otherUser.getId());

        //Then
        List<User> expUserFriends = Collections.EMPTY_LIST;
        List<User> expOtherUserFriends = List.of(user);
        assertAll("Проверка списка друзей",
                () -> assertEquals(expUserFriends, userFriends,
                        String.format("Неверный список друзей пользователя %s", user)),
                () -> assertEquals(expOtherUserFriends, otherUserFriends,
                        String.format("Неверный список друзей пользователя %s", otherUser))
        );
    }

    @Test
    void test6_removeFriendsInvalidIds() throws ValidationException {
        //Given
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>());
        User otherUser = new User(2, "email2@email.com", "login2",
                "name2", LocalDate.of(2000,2,1)
                , new HashSet<>());
        user.addFriend(2);
        otherUser.addFriend(1);
        service.addUser(user);
        service.addUser(otherUser);

        //When

        //Then
        assertAll("Проверка удаления из друзей пользователя с invalid параметрами",
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.deleteFriend(user.getId(), 3),
                        "пользователя otherUser не существует"),
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.deleteFriend(4, otherUser.getId()),
                        "пользователя User не существует"),
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.deleteFriend(user.getId(), null),
                        "пользователя otherUser не существует"),
                () -> assertThrows(UserNotFoundException.class,
                        () -> service.deleteFriend(null, otherUser.getId()),
                        "пользователя User не существует")

        );
    }

    @Test
    void test7_findCommonFriends() throws ValidationException, UserNotFoundException {
        //Given
        User user = new User(1, "email@email.com", "login",
                "name", LocalDate.of(2000,1,1)
                , new HashSet<>(List.of(2,3,4)));
        User user2 = new User(2, "email2@email.com", "login2",
                "name2", LocalDate.of(2000,2,1)
                , new HashSet<>(List.of(1,3,4)));
        User user3 = new User(3, "email3@email.com", "login3",
                "name3", LocalDate.of(2000,3,1)
                , new HashSet<>(List.of(1,2)));
        User user4 = new User(4, "email4@email.com", "login4",
                "name4", LocalDate.of(2000,4,1)
                , new HashSet<>(List.of(1,2)));
        service.addUser(user);
        service.addUser(user2);
        service.addUser(user3);
        service.addUser(user4);

        //When
        final Collection<User> commonFriends = service.findCommonFriends(
                user.getId(), user2.getId());

        //Then
        Set<User> expCommonFriends = Set.of(user3, user4);
        assertAll("Проверка списка общих друзей",
                () -> assertEquals(2, commonFriends.size(),
                        "Неверное кол-во друзей"),
                () -> assertEquals(expCommonFriends, new HashSet<>(commonFriends),
                        "Отличается состав общих друзей")
        );
    }

}