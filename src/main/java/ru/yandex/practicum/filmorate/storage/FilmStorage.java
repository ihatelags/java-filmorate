package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    List<Film> getPopularFilms(Integer count);


}