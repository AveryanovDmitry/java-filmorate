package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage {

    void add(Set<Genre> genres, Integer film);

    String findById(Integer id);

    Set<Genre> findAll();

    Set<Genre> getFilmGenresByFilmId(Integer filmId);

    boolean deleteGenre(Integer idFilm, Integer idGenre);

    void updateGenre(Set<Genre> genres, Integer idFilm);

    Map<Integer, LinkedHashSet<Genre>> getGenresListFilmsId(List<Integer> idFilms);
}