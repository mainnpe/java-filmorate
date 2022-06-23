package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserStorageTest {
    private final UserStorage storage;

    @Test
    public void test1_createAndGetUser() {
        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));

        //When
        User savedUser = storage.addUser(user);
        User getUser = storage.findUser(savedUser.getId());
        //Then
        assertAll("Check saved user",
                () -> assertEquals(1, savedUser.getId(),
                        "field id incorrect"),
                () -> assertEquals(user.getEmail(), savedUser.getEmail(),
                        "field email incorrect"),
                () -> assertEquals(user.getLogin(), savedUser.getLogin(),
                        "field login incorrect"),
                () -> assertEquals(user.getBirthday(), savedUser.getBirthday(),
                        "field birthday incorrect"));
        assertAll("Check get user by Id",
                () -> assertEquals(1, getUser.getId(),
                        "field id incorrect"),
                () -> assertEquals(user.getEmail(), getUser.getEmail(),
                        "field email incorrect"),
                () -> assertEquals(user.getLogin(), getUser.getLogin(),
                        "field login incorrect"),
                () -> assertEquals(user.getBirthday(), getUser.getBirthday(),
                        "field birthday incorrect"));

    }
    
    @Test
    void test2_createAndGet2ndUser() {
        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User user2 = new User("2ndmail@mail.ru","2nddolore",
                "2ndNick Name", LocalDate.of(1952, Month.AUGUST,20));
        User savedUser = storage.addUser(user);
	
        //When
        User savedUser2 = storage.addUser(user2);
        User getUser = storage.findUser(savedUser.getId());
        User getUser2 = storage.findUser(savedUser2.getId());
        
        //Then
        assertAll("Check saved user1",
                () -> assertEquals(1, getUser.getId(),
                        "field id incorrect"),
                () -> assertEquals(user.getEmail(), getUser.getEmail(),
                        "field email incorrect"),
                () -> assertEquals(user.getLogin(), getUser.getLogin(),
                        "field login incorrect"),
                () -> assertEquals(user.getBirthday(), getUser.getBirthday(),
                        "field birthday incorrect"));
        assertAll("Check saved user2",
                () -> assertEquals(2, getUser2.getId(),
                        "field id incorrect"),
                () -> assertEquals(user2.getEmail(), getUser2.getEmail(),
                        "field email incorrect"),
                () -> assertEquals(user2.getLogin(), getUser2.getLogin(),
                        "field login incorrect"),
                () -> assertEquals(user2.getBirthday(), getUser2.getBirthday(),
                        "field birthday incorrect"));
    }

    @Test
    void test3_updateValidUser() {
        System.out.println("User storage class - " + storage.getClass());
        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User savedUser = storage.addUser(user);

        //When
        User updUser = new User("UPDmail@mail.ru","UPDdolore",
                "UPD Nick Name", LocalDate.of(1956, Month.AUGUST,23));
        updUser.setId(savedUser.getId());
        User updatedUser = storage.updateUser(updUser);

        //Then
        assertAll("Check updated user",
                () -> assertEquals(updUser.getId(), updatedUser.getId(),
                        "field id incorrect"),
                () -> assertEquals(updUser.getEmail(), updatedUser.getEmail(),
                        "field email incorrect"),
                () -> assertEquals(updUser.getLogin(), updatedUser.getLogin(),
                        "field login incorrect"),
                () -> assertEquals(updUser.getBirthday(), updatedUser.getBirthday(),
                        "field birthday incorrect"));
    }

    @Test
    void test4_findAllUsers() {
        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User user2 = new User("2ndmail@mail.ru","2nddolore",
                "2ndNick Name", LocalDate.of(1952, Month.AUGUST,20));
        User savedUser1 = storage.addUser(user);
        User savedUser2 = storage.addUser(user2);

        //When
        Collection<User> users = storage.findAllUsers();

        //Then
        assertAll("Check find All users",
                () -> assertEquals(2, users.size(),
                        "incorrect number of users"),
                () -> assertTrue(users.contains(savedUser1), "user1 not found"),
                () -> assertTrue(users.contains(savedUser2), "user2 not found")
        );
    }

    @Test
    void test5_findFriends() {
        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User user2 = new User("2ndmail@mail.ru","2nddolore",
                "2ndNick Name", LocalDate.of(1952, Month.AUGUST,20));
        User user3 = new User("3rdmail@mail.ru","3rddolore",
                "3rdNick Name", LocalDate.of(1953, Month.AUGUST,21));
        User savedUser1 = storage.addUser(user);
        User savedUser2 = storage.addUser(user2);
        User savedUser3 = storage.addUser(user3);

        //When
        storage.addFriend(savedUser1.getId(), savedUser2.getId());
        storage.addFriend(savedUser1.getId(), savedUser3.getId());
        Collection<User> user1Friends = storage.findFriends(savedUser1.getId());

        //Then
        assertAll("Check find user friends",
                () -> assertEquals(2, user1Friends.size(),
                        "incorrect number of users"),
                () -> assertTrue(user1Friends.contains(savedUser2),
                        "user2 not found in friends"),
                () -> assertTrue(user1Friends.contains(savedUser3),
                        "user3 not found in friends")
        );
    }

    @Test
    void test6_findCommonFriends() {
        //Given
        User user = new User("mail@mail.ru","dolore",
                "Nick Name", LocalDate.of(1946, Month.AUGUST,20));
        User user2 = new User("2ndmail@mail.ru","2nddolore",
                "2ndNick Name", LocalDate.of(1952, Month.AUGUST,20));
        User user3 = new User("3rdmail@mail.ru","3rddolore",
                "3rdNick Name", LocalDate.of(1953, Month.AUGUST,21));
        User user4 = new User("4thmail@mail.ru","4thdolore",
                "4thNick Name", LocalDate.of(1964, Month.AUGUST,22));
        User savedUser1 = storage.addUser(user);
        User savedUser2 = storage.addUser(user2);
        User savedUser3 = storage.addUser(user3);
        User savedUser4 = storage.addUser(user4);

        //When
        //user 1 add friends: user2,user3,user4
        storage.addFriend(savedUser1.getId(), savedUser2.getId());
        storage.addFriend(savedUser1.getId(), savedUser3.getId());
        storage.addFriend(savedUser1.getId(), savedUser4.getId());

        //user 3 add friends: user1,user2,user4
        storage.addFriend(savedUser3.getId(), savedUser1.getId());
        storage.addFriend(savedUser3.getId(), savedUser2.getId());
        storage.addFriend(savedUser3.getId(), savedUser4.getId());
        //find user1 and user3 common friends
        Collection<User> commonFriends = storage.findCommonFriends(
                savedUser1.getId(), savedUser3.getId());

        //Then
        assertAll("Check find common friends",
                () -> assertEquals(2, commonFriends.size(),
                        "incorrect number of users"),
                () -> assertTrue(commonFriends.contains(savedUser2),
                        "user2 not found in common friends"),
                () -> assertTrue(commonFriends.contains(savedUser4),
                        "user4 not found in common friends")
        );
    }



    @Test
    void nullTest() {
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
    void hashAndEqualTest() {
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