package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @Override
    public Film create(Film film) {
        film.setId(id);
        film.setLikes(new HashSet<>());
        films.put(id, film);
        id++;
        return film;
    }

    @Override
    public Film save(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film.getId());
        }
        films.put(film.getId(), film);
        log.info("Данные фильма {} (id={}) успешно обновлены", film.getName(), film.getId());
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Collection<Film> getFilmsByIds(Collection<Integer> filmIds) {
        return null;
    }

    @Override
    public Collection<Film> loadFoundFilms(String query, List<String> by) {
        return null;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.get(id) == null) {
            throw new FilmNotFoundException(id);
        }
        return films.get(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
        save(film);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        film.getLikes().remove(userId);
        save(film);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return getAll()
                .stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(int directorId, String sortBy) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return null;
    }

    @Override
    public void deleteUserById(int id) {

    }

    @Override
    public Collection<Film> getPopularFilmsWithGenreAndYear(Integer limit, Integer genreID, Integer year) {
        return null;
    }

    @Override
    public Collection<Film> getPopularFilmsWithGenre(Integer count, Integer genreId) {
        return null;
    }

    @Override
    public Collection<Film> getPopularFilmsWithYear(Integer count, Integer year) {
        return null;
    }
}
