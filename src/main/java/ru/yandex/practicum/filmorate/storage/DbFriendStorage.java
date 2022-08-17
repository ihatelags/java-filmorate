package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Primary
public class DbFriendStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public DbFriendStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }


    private User loadFriends(User user) {
        SqlRowSet sqlRows = jdbcTemplate.queryForRowSet(
                "SELECT friend_id FROM friendships WHERE user_id = ?",
                user.getId()
        );
        while (sqlRows.next()) {
            user.getFriends().add((long) sqlRows.getInt("friend_id"));
        }
        return user;
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        String sql = "INSERT INTO friendships (user_id, friend_id)" +
                "VALUES (?, ?);";
        jdbcTemplate.update(sql,
                userId,
                friendId
        );
    }

    @Override
    public void removeFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            return;
        }
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT * FROM friendships WHERE user_id = ? ;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriendship(rs), userId).stream()
                .map(Friendship::getFriendId)
                .map(this::getUserFromStorage)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        Set<User> commonFriends = new HashSet<>(getFriends(id));
        try {
            commonFriends.retainAll(getFriends(otherId));
            return new ArrayList<>(commonFriends);
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    private Friendship makeFriendship(ResultSet rs) throws SQLException {
        return new Friendship(rs.getInt("user_id"), rs.getInt("friend_id"));
    }

    private User getUserFromStorage(Long userId) {
        return userStorage.getUser(userId);
    }
}
