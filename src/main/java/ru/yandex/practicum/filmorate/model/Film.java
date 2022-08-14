package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Builder
public class Film {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @JsonIgnore
    private Set<Long> likes;
    private int rate;
    private MpaRating mpa;
    private List<Genre> genres;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, HashSet<Long> likes, int rate, MpaRating mpa) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.description = description;
        this.duration = duration;
        this.likes = likes;
        this.rate = rate;
        this.mpa = mpa;
    }

    public int countLikes() {
        if (likes == null) {
            return 0;
        } else return likes.size();
    }


}