package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {
    private FilmStorage filmStorage;
    private FilmService filmService;
    private FilmController filmController;
    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private Film film;
    private Film film2;
    private User user;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void init() {
        film = Film.builder()
                .name("film")
                .description("description")
                .releaseDate(LocalDate.of(1980, 1, 1))
                .duration(120)
                .likes(new HashSet<>())
                .build();

        film2 = Film.builder()
                .name("film2")
                .description("description2")
                .releaseDate(LocalDate.of(1980, 2, 1))
                .duration(130)
                .likes(new HashSet<>())
                .build();

        user = User.builder()
                .email("user@java.com")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1999, 1, 1))
                .friends(new HashSet<>())
                .build();

        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
        userService = new UserService(userStorage);

        filmController = new FilmController(filmService);
        filmController.createFilm(film);
        filmController.createFilm(film2);

        userController = new UserController(userService);
        userController.createUser(user);
    }

    @Test
    public void createFilmTest() {
        assertEquals(film, filmController.getAllFilms().get(0));
        assertEquals(2, filmController.getAllFilms().size());
        var response = restTemplate.postForEntity("/films", film, Film.class);
        response = restTemplate.getForEntity("/films/1", Film.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
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

    @Test
    public void addAndDeleteLike() {
        filmController.addLike(film.getId(), user.getId());
        assertEquals(filmController.getFilm(film.getId()).getLikes().size(), 1);
        filmController.deleteLike(film.getId(), user.getId());
        assertEquals(filmController.getFilm(film.getId()).getLikes().size(), 0);
    }

    @Test
    void getPopularFilmTest() {
        Film filmWithLike = filmController.addLike(film.getId(), user.getId());
        List<Film> popularFilmList = filmController.getPopularFilm(1);
        assertEquals(popularFilmList.contains(filmWithLike), true);
    }
}