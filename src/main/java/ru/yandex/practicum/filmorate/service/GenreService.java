package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre create(Genre genre) {
        genre = genreDao.create(genre);
        log.info("Жанр {} c id {} был успешно создан", genre.getName(), genre.getId());
        return genre;
    }

    public Genre getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

    public Collection<Genre> getAll() {
        return genreDao.getAll();
    }
}
