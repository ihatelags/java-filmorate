package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм c ид={} добавлен", film.getId());
        return films.get(film.getId());
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    @Override
    public Film updateFilm(Film film) {
        films.replace(film.getId(), film);
        log.info("Фильм c ид={} обновлен", film.getId());
        return films.get(film.getId());
    }

    @Override
    public Film addLike(Long id, Long userId) {
        Film film = films.get(id);
        film.getLikes().add(userId);
        return updateFilm(film);
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        Film film = films.get(id);
        film.getLikes().remove(userId);
        return updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::countLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}