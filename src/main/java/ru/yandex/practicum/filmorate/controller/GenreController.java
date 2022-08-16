package ru.yandex.practicum.filmorate.controller;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@Validated
@NoArgsConstructor
public class GenreController {
    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    private GenreStorage genreStorage;

    @Autowired
    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.debug("Общее количество жанров в справочнике : {}", genreStorage.getAllGenres().size());
        return genreStorage.getAllGenres();
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenreById(@PathVariable long genreId) {
        if (genreId < 1) {
            throw new NotFoundException("Genre with id=" + genreId + "not found");
        }
        log.debug("Get genre by id={}", genreId);
        return genreStorage.getGenre(genreId);
    }
}
