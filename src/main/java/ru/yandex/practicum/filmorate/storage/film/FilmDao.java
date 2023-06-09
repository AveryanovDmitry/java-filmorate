package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;

import java.util.Collection;
import java.util.List;

public interface FilmDao {
    Film create(Film film);

    Film save(Film film);

    Collection<Film> getAll();

    Collection<Film> getFilmsByIds(Collection<Integer> filmIds);

    Collection<Film> loadFoundFilms(String query, List<String> by);

    Film getFilmById(int id);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> getFilmsByDirectorId(int directorId, FilmSortBy sortBy);

    List<Film> getCommonFilms(int userId, int friendId);

    void deleteUserById(int id);

    Collection<Film> getPopularFilmsWithGenreAndYear(Integer limit, Integer genreID, Integer year);

    Collection<Film> getPopularFilmsWithGenre(Integer count, Integer genreId);

    Collection<Film> getPopularFilmsWithYear(Integer count, Integer year);
}
