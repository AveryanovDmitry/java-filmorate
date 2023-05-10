package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public Film add(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        log.info("Фильм, {} добавлен", film.getName());
        return films.get(film.getId());
    }

    public Film update(Film film) {
        checkId(film.getId());
        films.put(film.getId(), film);
        log.info("Фильм, {} обновлён", film.getName());
        return films.get(film.getId());
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public Optional<Film> getById(Integer id) {
        checkId(id);
        return Optional.of(films.get(id));
    }

    private void checkId(Integer id) {
        if (!films.containsKey(id)) {
            log.info("Фильм с id {} не найден", id);
            throw new MyValidationExeption(HttpStatus.NOT_FOUND, "Фильм с таким id не найден");
        }
    }
}
