package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

public interface UserStorage {
    HashMap<Long, User> getAllUsers();

    User createUser(User user);

    User getUser(Long id);

    User updateUser(User user);

    User addFriends(Long userId, Long friendId);

    User removeFriends(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long id, Long otherId);
}