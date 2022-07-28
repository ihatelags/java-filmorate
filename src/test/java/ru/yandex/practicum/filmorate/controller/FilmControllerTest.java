package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void init() {
        film = new Film();
        film.setId(1);
        film.setName("film");
        film.setDescription("description");
        film.setReleaseDate((LocalDate.of(1980, 1, 1)));
        film.setDuration(120);
        filmController = new FilmController();
        filmController.createFilm(film);
    }

    @Test
    public void createFilmTest() {
        assertEquals(film, filmController.getAllFilms().get(0));
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    public void updateFilmWrongIdTest() throws ValidationException {
        film.setId(2);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void updateFilmSuccessTest() {
        film.setName("updated name");
        filmController.updateFilm(film);
        assertEquals("updated name", filmController.getAllFilms().get(0).getName());
    }

    @Test
    public void emptyFilmNameTest() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void filmDescriptionMaxLengthTest() throws ValidationException {
        String longDesc = "a";
        while (longDesc.length() < 200){
            longDesc += longDesc;
        }
        film.setDescription(longDesc);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void filmReleaseDateTest()  throws ValidationException {
        film.setReleaseDate((LocalDate.of(1800, 1, 1)));
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void filmDurationTest()  throws ValidationException {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(film));
    }
}