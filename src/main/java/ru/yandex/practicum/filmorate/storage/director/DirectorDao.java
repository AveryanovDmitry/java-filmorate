package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorDao {
    Director create(Director director);

    Director save(Director director);

    Director getDirectorById(int id);

    Collection<Director> getAll();

    boolean deleteDirectorById(int id);

}
