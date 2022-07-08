package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int nextId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Пользователь уже существует " + user.getEmail());
            throw new ValidationException("Пользователь с почтой " + user.getEmail() + " уже существует");
        }
        user.setId(++nextId);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if(!users.containsKey(user.getId())){
            log.error("Ошибка валидации id пользователя");
            throw new ValidationException(MessageFormat.format("Пользователь c id: {0} не существует", user.getId()));
        }
        users.replace(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }

    private void validate(User user) {

        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации электронной почты пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации логина пользователя");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName().isEmpty()){
            log.warn("Имя пользователя не указано. Будет использован логин");
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации даты рождения пользователя");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        log.info("Валидация пользователя пройдена успешно");
    }

}