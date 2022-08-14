package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Friendship {
    private long userId;
    private long friendId;
}
