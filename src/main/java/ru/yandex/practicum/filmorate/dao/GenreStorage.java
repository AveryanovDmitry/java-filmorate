package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public interface GenreStorage {

    void add(Integer genre, Integer film);

    String findById(Integer id);

    List<Genre> findAll();

    List<Genre> getGenres(Integer filmId);

    boolean deleteGenre(Integer idFilm, Integer idGenre);
}