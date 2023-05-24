package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @JsonIgnore
    private Set<Integer> likes = new HashSet<>();

    public Film(String name, LocalDate releaseDate, String description, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(Integer idUser) {
        likes.add(idUser);
    }

    public void deleteLike(Integer idUser) {
        likes.remove(idUser);
    }

    public int getRate() {
        return likes.size();
    }
}
