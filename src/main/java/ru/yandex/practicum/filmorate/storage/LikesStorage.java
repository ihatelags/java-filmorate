package ru.yandex.practicum.filmorate.storage;

public interface LikesStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

}
