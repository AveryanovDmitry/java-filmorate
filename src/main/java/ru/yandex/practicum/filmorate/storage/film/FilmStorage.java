package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film save(Film film);

    Collection<Film> getAll();

    Collection<Film> loadFoundFilms(String query, List<String> by);

    Film getFilmById(int id);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> getFilmsByDirectorId(int directorId, String sortBy);

    List<Film> getCommonFilms(int userId, int friendId);

    void deleteUserById(int id);
}
