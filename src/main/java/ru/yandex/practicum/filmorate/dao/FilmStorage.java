package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Optional<Film> getById(Integer id);

    boolean addLike(Integer idFilm, Integer idUser);

    List<Film> mostPopulars(Integer limit);

    boolean deleteLike(Integer idFilm, Integer idUser);
}
