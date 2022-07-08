package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1);
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("user@java.com");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        userController = new UserController();
        userController.createUser(user);
    }

    @Test
    public void createUserTest() {
        assertEquals(user, userController.getAllUsers().get(0));
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    public void updateUserWrongIdTest() throws ValidationException {
        user.setId(2);
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
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
        User user2 = new User();
        user2.setId(2);
        user2.setName("Name2");
        user2.setLogin("Login2");
        user2.setEmail("user@java.com");
        user2.setBirthday(LocalDate.of(1999, 1, 1));
        assertThrows(ValidationException.class, () -> userController.createUser(user2));
    }

    @Test
    public void invalidUserEmailTest() throws ValidationException {
        user.setEmail("user java.com");
        assertThrows(ValidationException.class, () -> userController.updateUser(user));
    }
}