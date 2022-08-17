package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

@Repository
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
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

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select  GENRE_ID ,GENRE_NAME " +
                "FROM GENRES ";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
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
            String sqlQuery = "INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID) values (?,?)";
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
        List<Genre> genres = jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
        genres.sort(Comparator.comparing(Genre::getId));
        return genres;
    }

}
