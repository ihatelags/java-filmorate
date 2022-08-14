package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class DbFilmStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films LEFT OUTER JOIN ratings ON films.rating_id=ratings.rating_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT * FROM films LEFT OUTER JOIN ratings ON films.rating_id=ratings.rating_id " +
                "WHERE FILMS.film_id = ? ;";
        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, id);
        if (films.size() != 1) {
            return null;
        }
        Film film = films.get(0);
        film.setGenres(new ArrayList<>(loadFilmGenre(film)));
        return films.get(0);
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (film_name, description, release_date, duration, rate, rating_id)" +
                "VALUES (?, ?, ?, ?, ?, ?);" ;
        SqlRowSet sqlRows = jdbcTemplate.queryForRowSet("SELECT COUNT(film_id) AS count FROM films ;");
        sqlRows.next();
        film.setId(sqlRows.getInt("count") + 1);
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId()
        );
        setFilmGenre(film);
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
        setFilmGenre(film);
        return getFilm(film.getId());
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ? ;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);" ;
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?;" ;
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
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

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "select * FROM films " +
                "LEFT JOIN films_likes L on films.film_id = L.film_id " +
                "JOIN ratings ON ratings.rating_id=films.rating_id " +
                "GROUP BY film_name " +
                "ORDER BY COUNT (L.USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    @Override
    public void setFilmGenre(Film film) {
        long id = film.getId();
        String sqlDelete = "delete from FILMS_GENRE where FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, id);
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            String sqlQuery = "INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID) values (?,?) ";
            jdbcTemplate.update(sqlQuery, id, genre.getId());
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
    }

    @Override
    public List<Genre> loadFilmGenre(Film film) {
        long filmId = film.getId();
        String sqlQuery = "SELECT GENRE_ID, GENRE_NAME " +
                "FROM GENRES " +
                "WHERE GENRE_ID IN (SELECT GENRE_ID " +
                "FROM FILMS_GENRE " +
                "WHERE FILM_ID = ?)";

        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

    public Genre getGenre(long id) {
        final String sqlQuery = "select GENRE_ID ,GENRE_NAME " +
                "FROM GENRES " +
                "where GENRE_ID = ?";
        final List<Genre> genres = jdbcTemplate.query(sqlQuery, this::makeGenre, id);
        if (genres.size() != 1) {
            return null;
        }
        return genres.get(0);
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "select  GENRE_ID ,GENRE_NAME " +
                "FROM GENRES ";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    private MpaRating makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(rs.getInt("rating_id"),
                rs.getString("rating_name")
        );
    }

    public MpaRating getMpa(long id) {
        final String sqlQuery = "select * FROM ratings " +
                "where rating_id = ?";
        final List<MpaRating> mpas = jdbcTemplate.query(sqlQuery, this::makeMpa, id);
        if (mpas.size() != 1) {
            return null;
        }
        return mpas.get(0);
    }

    public List<MpaRating> getAllMpa() {
        final String sqlQuery = "select * FROM ratings";
        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }
}