package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film getFilm(Long id);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    List<Film> getPopularFilms(Integer count);

    void setFilmGenre(Film film);

    List<Genre> loadFilmGenre(Film film);
}