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
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;


import java.util.List;

@RestController
@Validated
@NoArgsConstructor
public class MpaController {
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);

    private MpaStorage mpaStorage;

    @Autowired
    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    /**
     * получение рейтинга(MPA) по идентификатору
     */
    @GetMapping("/mpa/{id}")
    public MpaRating getMpa(@PathVariable long id) {
        if (id < 1) {
            throw new NotFoundException("MPA with id=" + id + "not found");
        }
        log.debug("Get MPA by id={}", id);
        return mpaStorage.getMpa(id);
    }

    /**
     * получение списка всех рейтингов(MPA)
     */
    @GetMapping("/mpa")
    public List<MpaRating> getAllMpa() {
        log.debug("Общее количество рейтингов MPA : {}", mpaStorage.getAllMpa().size());
        return mpaStorage.getAllMpa();
    }

}
