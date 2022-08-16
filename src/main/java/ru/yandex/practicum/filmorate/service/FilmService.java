package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private long nextId = 0;
    private final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        validate(film);
        film.setId(++nextId);
        if(film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return filmStorage.createFilm(film);
    }

    public Film getFilm(Long id) {
        Film film = filmStorage.getFilm(id);
        validateFilmExists(id);
        return film;
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film updateFilm(Film film) {
        validateFilmExists(film.getId());
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        validate(film);
        validateUserExists(userId);
        likesStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        validate(film);
        validateUserExists(userId);
        likesStorage.removeLike(id, userId);
    }

    private void validate(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Ошибка валидации названия фильма c ид={}", film.getId());
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации описания фильма c ид={}", film.getId());
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.error("Ошибка валидации даты релиза фильма c ид={}", film.getId());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 1) {
            log.error("Неверная продолжительность фильма c ид={}", film.getId());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        log.info("Валидация фильма c ид={} пройдена успешно ", film.getId());
    }

    private void validateFilmExists(Long filmId) {
        try {
            if (!filmStorage.getAllFilms().contains(filmStorage.getFilm(filmId))) {
                log.error("Ошибка валидации фильма c ид={}", filmId);
                throw new NotFoundException(MessageFormat.format("Фильм c id: {0} не существует", filmId));
            }
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException(MessageFormat.format("Фильм c id: {0} не существует", filmId));
        }
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
