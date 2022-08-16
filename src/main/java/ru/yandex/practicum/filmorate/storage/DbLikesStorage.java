package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;


@Repository
@Primary
public class DbLikesStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbLikesStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Film loadLikes(Film film) {
        SqlRowSet sqlRows = jdbcTemplate.queryForRowSet(
                "SELECT user_id FROM films_likes WHERE film_id = ?;",
                film.getId()
        );
        while (sqlRows.next()) {
            film.getLikes().add((long) sqlRows.getInt("user_id"));
        }
        return film;
    }


}