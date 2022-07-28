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
    private long nextId = 0;
    private final HashMap<Long, User> users = new HashMap<>();

    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        validate(user);
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Пользователь c ид={} уже существует. Не уникальная почта {}", user.getId(), user.getEmail());
            throw new ValidationException("Пользователь с почтой " + user.getEmail() + " уже существует");
        }
        user.setId(++nextId);
        users.put(user.getId(), user);
        log.info("Пользователь c ид={} добавлен", user.getId());
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if(!users.containsKey(user.getId())){
            log.error("Ошибка валидации пользователя c ид={}", user.getId());
            throw new ValidationException(MessageFormat.format("Пользователь c id: {0} не существует", user.getId()));
        }
        users.replace(user.getId(), user);
        log.info("Пользователь c ид={} обновлен", user.getId());
        return user;
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

}