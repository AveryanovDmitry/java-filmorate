package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    public Set<Genre> getAll() {
        return genreStorage.findAll();
    }

    public Set<Genre> getFilmGenresByFilmId(Integer id) {
        return genreStorage.getFilmGenresByFilmId(id);
    }

    public Map<Integer, LinkedHashSet<Genre>> getGenresListFilmsId(List<Integer> filmsId) {
        return genreStorage.getGenresListFilmsId(filmsId);
    }

    public Genre getById(Integer id) {
        return new Genre(id, genreStorage.findById(id));
    }

    public void addGenres(Set<Genre> genres, Integer id) {
        genreStorage.add(genres, id);
        log.info("Добавлены жанры: {}", genres);
    }

    public void update(Set<Genre> genres, Integer id) {
        genreStorage.updateGenre(genres, id);
    }
}