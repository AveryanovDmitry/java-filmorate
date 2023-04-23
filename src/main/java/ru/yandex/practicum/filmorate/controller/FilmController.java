package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate VALID_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    private int id = 1;

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        checkReleaseDate(film);
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        log.info("Фильм, {} добавлен", film.getName());
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            checkReleaseDate(film);
            films.put(film.getId(), film);
            log.info("Фильм, {} обновлён", film.getName());
            return films.get(film.getId());
        } else {
            log.info("Фильм с id {} не найден", film.getId());
            throw new MyValidationExeption(HttpStatus.NOT_FOUND, "Фильм с таким id не найден");
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(VALID_DATE)) {
            throw new MyValidationExeption(HttpStatus.BAD_REQUEST,
                    "дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
