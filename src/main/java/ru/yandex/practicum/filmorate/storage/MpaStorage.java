package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaStorage {
    MpaRating getMpa(Long id);

    List<MpaRating> getAllMpa();
}
