package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {
    Genre create(Genre genre);

    Genre getGenreById(int id);

    Collection<Genre> getAll();

}
