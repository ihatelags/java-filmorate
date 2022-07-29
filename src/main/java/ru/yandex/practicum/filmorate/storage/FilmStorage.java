package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Film updateFilm(Film film);

    Film addLike(Long id, Long userId);

    Film removeLike(Long id, Long userId);

    List<Film> getPopularFilms(Integer count);
}