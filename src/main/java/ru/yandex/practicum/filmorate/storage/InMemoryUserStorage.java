package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь c ид={} добавлен", user.getId());
        return users.get(user.getId());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    public User updateUser(User user) {
        users.replace(user.getId(), user);
        log.info("Пользователь c ид={} обновлен", user.getId());
        return users.get(user.getId());
    }

    @Override
    public User addFriends(Long userId, Long friendId) {
        addFriend(friendId, userId);
        return addFriend(userId, friendId);
    }

    @Override
    public User removeFriends(Long userId, Long friendId) {
        removeFriend(friendId, userId);
        return removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        HashSet<Long> friendIdSet = users.get(userId).getFriends();
        List<User> friendList = new ArrayList<>();

        for (Long friendId : friendIdSet) {
            friendList.add(users.get(friendId));
        }

        return friendList;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        List<User> commonFriends = new ArrayList<>();
        HashSet<Long> friendsOfUser;
        HashSet<Long> friendsOfAnotherUser;

        if (id != null && otherId != null) {
            friendsOfUser = users.get(id).getFriends();
            friendsOfAnotherUser = users.get(otherId).getFriends();
        } else {
            return commonFriends;
        }

        if (friendsOfUser == null) return commonFriends;

        Set<Long> commonFriendsIds = new HashSet<>(friendsOfUser);
        commonFriendsIds.retainAll(friendsOfAnotherUser);

        for (Long commonFriendsId : commonFriendsIds) {
            commonFriends.add(users.get(commonFriendsId));
        }

        return commonFriends;
    }

    private User addFriend(Long userId, Long friendId) {
        User user = users.get(friendId);
        HashSet<Long> friends = user.getFriends();

        if (friends == null) {
            friends = new HashSet<>();
        }

        friends.add(userId);
        user.setFriends(friends);
        return updateUser(user);
    }

    private User removeFriend(Long userId, Long friendId) {
        User user = users.get(friendId);
        HashSet<Long> friends = user.getFriends();

        if (friends != null) {
            friends.remove(userId);
        }

        user.setFriends(friends);
        return updateUser(user);
    }
}