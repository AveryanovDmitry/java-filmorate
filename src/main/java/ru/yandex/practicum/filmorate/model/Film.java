package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;

    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200, message = "максимальная длина 200 символов")
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "продолжительность должна быть положительной")
    private Integer duration;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres;

    public Film(String name, String description, LocalDate releaseDate, Integer duration,
                Mpa mpa, LinkedHashSet<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = Objects.requireNonNullElseGet(genres, LinkedHashSet::new);
    }
}
