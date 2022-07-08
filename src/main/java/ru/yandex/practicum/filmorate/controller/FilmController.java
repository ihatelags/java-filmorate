package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int nextId = 0;
    private final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(++nextId);
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (!films.containsKey(film.getId())) {
            log.error("Ошибка валидации id фильма");
            throw new ValidationException(MessageFormat.format("Фильм c id: {0} не существует", film.getId()));
        }
        films.replace(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }

    private void validate(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Ошибка валидации названия фильма");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации описания фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.error("Ошибка валидации даты релиза фильма");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 1) {
            log.error("Неверная продолжительность фильма id={}", film.getId());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

    }
}