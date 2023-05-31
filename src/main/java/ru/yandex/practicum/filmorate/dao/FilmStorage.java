package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    void update(Film film);

    List<Film> getFilms();

    Optional<Film> getById(Integer id);

    List<Film> mostPopulars(Integer limit);

    void checkId(Integer id);
}
