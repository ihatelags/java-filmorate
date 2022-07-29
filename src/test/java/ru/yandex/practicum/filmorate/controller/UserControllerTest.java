package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private User user;
    private User user2;

    @BeforeEach
    void init() {
        user = User.builder()
                .email("user@java.com")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1999, 1, 1))
                .friends(new HashSet<>())
                .build();

        user2 = User.builder()
                .email("user2@java.com")
                .login("Login2")
                .name("Name2")
                .birthday(LocalDate.of(1999, 2, 1))
                .friends(new HashSet<>())
                .build();

        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
        userController.createUser(user);
    }

    @Test
    public void createUserTest() {
        assertEquals(user, userController.getAllUsers().get(0));
        assertEquals(1, userController.getAllUsers().size());
    }


    @Test
    public void updateUserSuccessTest() {
        user.setName("updated name");
        userController.updateUser(user);
        assertEquals("updated name", userController.getAllUsers().get(0).getName());
    }

    @Test
    public void emptyUserNameTest() {
        user.setName("");
        userController.updateUser(user);
        assertEquals(user.getLogin(), userController.getAllUsers().get(0).getName());
    }

    @Test
    public void emptyUserLoginTest() throws ValidationException {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    public void userUserBirthdayTest() throws ValidationException {
        user.setBirthday((LocalDate.of(2200, 1, 1)));
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    public void nonUniqueUserEmailTest() throws ValidationException {
        user2.setEmail("user@java.com");
        assertThrows(ValidationException.class, () -> userController.createUser(user2));
    }

    @Test
    public void invalidUserEmailTest() throws ValidationException {
        user.setEmail("user java.com");
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }

    @Test
    void addFriendsTest() {
        userController.createUser(user2);
        userController.addFriends(user.getId(), user2.getId());
        User userWithFriend = userController.getUser(user.getId());
        assertEquals(userWithFriend.getFriends().contains(user2.getId()), true);
    }

    @Test
    void removeFriendsTest() {
        userController.createUser(user2);
        userController.addFriends(user.getId(), user2.getId());
        userController.removeFriends(user.getId(), user2.getId());
        User userWithFriend = userController.getUser(user.getId());
        assertEquals(userWithFriend.getFriends().contains(user2.getId()), false);
        assertEquals(userWithFriend.getFriends().size(), 0);
    }

    @Test
    void getCommonFriendsTest() {
        userController.createUser(user2);

        User commonFriend = User.builder()
                .email("user3@java.com")
                .login("Login3")
                .name("Name3")
                .birthday(LocalDate.of(1999, 2, 1))
                .friends(new HashSet<>())
                .build();
        userController.createUser(commonFriend);

        userController.addFriends(user.getId(), commonFriend.getId());
        userController.addFriends(commonFriend.getId(), user2.getId());

        List<User> friends = userController.getCommonFriends(user.getId(), user2.getId());
        assertEquals(friends.contains(commonFriend),true);

    }
}