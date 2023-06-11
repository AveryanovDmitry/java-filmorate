package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Director create(Director director) {
        director = directorDao.create(director);
        log.info("Директор {} с id {} был успешно создан", director.getName(), director.getId());
        return director;
    }

    public Director save(Director director) {
        director = directorDao.save(director);
        log.info("Данные директора {} (id={}) успешно обновлены", director.getName(), director.getId());
        return director;
    }

    public Director getDirectorById(int id) {
        return directorDao.getDirectorById(id);
    }

    public Collection<Director> getAll() {
        return directorDao.getAll();
    }

    public void deleteDirectorById(int id) {
        if (directorDao.deleteDirectorById(id)) {
            log.info("Директор с id - {} был успешно удален", id);
        } else {
            log.info("Не удалось удалить директора с id - {}", id);
        }
    }
}
