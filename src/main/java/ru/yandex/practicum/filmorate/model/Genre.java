package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
public class Genre {
    long id;
    String name;

    public Genre(Long id) {
        this.id = id;
    }
}
