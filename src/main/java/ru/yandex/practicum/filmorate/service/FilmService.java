package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmBadReleaseDateException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDao directorDao;

    private final FeedDao feedDao;
    private final LocalDate minFilmReleaseDate = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, @Qualifier("UserDbStorage") UserStorage userStorage,
                       DirectorDao directorDao, FeedDao feedDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorDao = directorDao;
        this.feedDao = feedDao;
    }

    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.addLike(filmId, userId);
        feedDao.addFeedList(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        filmStorage.deleteLike(filmId, userId);
        feedDao.addFeedList(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public Collection<Film> getFilmsByDirectorId(int directorId, String sortBy) {
        directorDao.getDirectorById(directorId);
        return filmStorage.getFilmsByDirectorId(directorId, sortBy);
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (genreId == null && year == null) {
            return filmStorage.getPopularFilms(count);
        } else if (genreId != null && year == null) {
            return filmStorage.getPopularFilmsWithGenre(count, genreId);
        } else if (genreId == null) {
            return filmStorage.getPopularFilmsWithYear(count, year);
        }
        return filmStorage.getPopularFilmsWithGenreAndYear(count, genreId, year);
    }

    public Film create(Film film) {
        validateFilm(film);
        film = filmStorage.create(film);
        log.info("Фильм {} (id={}) успешно создан", film.getName(), film.getId());
        return film;
    }

    public Film save(Film film) {
        validateFilm(film);
        film.setLikes(filmStorage.getFilmById(film.getId()).getLikes());
        return filmStorage.save(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }


    public Collection<Film> getFilmsByIds(Collection<Integer> filmIds) {
        return filmStorage.getFilmsByIds(filmIds);
    }

    public Collection<Film> searchFilms(String query, List<String> by) {
        return filmStorage.loadFoundFilms(query, by);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    private void validateFilm(Film film) {
        if (!film.getReleaseDate().isAfter(minFilmReleaseDate)) {
            throw new FilmBadReleaseDateException(String.format("Дата релиза должна быть позже %s", minFilmReleaseDate));
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userStorage.getUserById(userId).getId(),
                userStorage.getUserById(friendId).getId());
    }

    public void deleteUserById(int id) {
        filmStorage.deleteUserById(id);
    }
}
