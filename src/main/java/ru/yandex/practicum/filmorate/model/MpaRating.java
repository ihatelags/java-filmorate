package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class MpaRating {
    long id;
    String name;

    public MpaRating(Long id) {
        this.id = id;
    }
}
