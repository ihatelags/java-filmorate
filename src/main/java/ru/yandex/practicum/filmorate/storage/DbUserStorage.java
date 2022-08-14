package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
public class DbUserStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ;";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    public User getUser(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ? ;";
        return jdbcTemplate.query(sql, this::makeUser, id).get(0);
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, user_name, birthday) VALUES (?, ?, ?, ?);" ;
        SqlRowSet sqlRows = jdbcTemplate.queryForRowSet("SELECT COUNT(user_id) AS count FROM users;");
        sqlRows.next();
        user.setId(sqlRows.getInt("count") + 1);
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ?" +
                "where user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUser(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ? ;";
        jdbcTemplate.update(sql, id);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("USER_ID"));
        user.setEmail(rs.getString("EMAIL"));
        user.setLogin(rs.getString("LOGIN"));
        user.setName(rs.getString("USER_NAME"));
        user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
        return user;
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
                "VALUES (?, ?);" ;
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
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        if (getUser(id) == null || getUser(otherId) == null) {
            String message = ("Пользователь не найден");
            log.warn(message);
            throw new NotFoundException(message);
        }
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
}