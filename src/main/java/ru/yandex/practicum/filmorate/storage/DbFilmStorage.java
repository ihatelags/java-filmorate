package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public DbFilmStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films LEFT OUTER JOIN ratings ON films.rating_id=ratings.rating_id";
        return jdbcTemplate.query(sql, this::loadFilm);
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT * FROM films LEFT OUTER JOIN ratings ON films.rating_id=ratings.rating_id " +
                "WHERE FILMS.film_id = ? ;";
        List<Film> films = jdbcTemplate.query(sql, this::loadFilm, id);
        if (films.size() != 1) {
            return null;
        }
        Film film = films.get(0);
        film.setGenres(new LinkedHashSet<>(getGenresFromStorage(film)));
        return films.get(0);
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("RATING_ID", film.getMpa().getId());

        film.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        genreStorage.setFilmGenre(film);
        return getFilm(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET " +
                "film_name = ?, description = ?, release_date = ?, duration = ?, rate = ?, rating_id = ?" +
                "where film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()
        );
        genreStorage.setFilmGenre(film);
        return getFilm(film.getId());
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ? ;";
        jdbcTemplate.update(sql, id);
    }


    private Film loadFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(),
                rs.getInt("rate"),
                new MpaRating(rs.getLong("rating_id"), rs.getString("rating_name"))
        );
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "select * FROM films " +
                "LEFT JOIN films_likes L on films.film_id = L.film_id " +
                "JOIN ratings ON ratings.rating_id=films.rating_id " +
                "GROUP BY film_name " +
                "ORDER BY COUNT (L.USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::loadFilm, count);
    }

    private List<Genre> getGenresFromStorage(Film film) {
        return genreStorage.loadFilmGenre(film);
    }

}