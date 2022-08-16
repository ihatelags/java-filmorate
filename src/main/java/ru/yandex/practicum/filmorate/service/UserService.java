package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private Long nextId = 0L;
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long userId) {
        validateUserExists(userId);
        return userStorage.getUser(userId);
    }

    public User createUser(User user) {
        validate(user);
        if (userStorage.getAllUsers().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Пользователь c ид={} уже существует. Неуникальная почта {}", user.getId(), user.getEmail());
            throw new ValidationException("Пользователь с почтой " + user.getEmail() + " уже существует");
        }
        user.setId(++nextId);
        user.setFriends(new HashSet<>());
        userStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) {
        validateUserExists(user.getId());
        validate(user);
        userStorage.updateUser(user);
        return user;
    }

    public void addFriends(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        friendStorage.addFriends(userId, friendId);
    }

    public void removeFriends(Long userId, Long friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);
        friendStorage.removeFriends(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        validateUserExists(userId);
        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        validateUserExists(userId);
        validateUserExists(otherId);
        return friendStorage.getCommonFriends(userId, otherId);
    }

     private void validate(User user) {

        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации электронной почты пользователя c ид={}", user.getId());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации логина пользователя c ид={}", user.getId());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName().isEmpty()){
            log.warn("Имя пользователя c ид={} не указано. Будет использован логин", user.getId());
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации даты рождения пользователя c ид={}", user.getId());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        log.info("Валидация пользователя c ид={} пройдена успешно ", user.getId());
    }

    private void validateUserExists(Long userId) {
        try {
            if (!userStorage.getAllUsers().contains(userStorage.getUser(userId))) {
                log.error("Ошибка валидации пользователя c ид={}", userId);
                throw new NotFoundException(MessageFormat.format("Пользователь c id: {0} не существует", userId));
            }
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException(MessageFormat.format("Пользователь c id: {0} не существует", userId));
        }

    }
}