package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmBadReleaseDateException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmSortBy;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.user.UserDao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmDao filmDao;
    private final UserDao userDao;
    private final DirectorDao directorDao;

    private final FeedDao feedDao;
    private final LocalDate minFilmReleaseDate = LocalDate.of(1895, 12, 28);

    public void addLike(int filmId, int userId) {
        userDao.getUserById(userId);
        filmDao.addLike(filmId, userId);
        feedDao.addFeedList(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public void deleteLike(int filmId, int userId) {
        userDao.getUserById(userId);
        filmDao.deleteLike(filmId, userId);
        feedDao.addFeedList(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public Collection<Film> getFilmsByDirectorId(int directorId, FilmSortBy sortBy) {
        directorDao.getDirectorById(directorId);
        return filmDao.getFilmsByDirectorId(directorId, sortBy);
    }

    public Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year) {
        if (genreId == null && year == null) {
            return filmDao.getPopularFilms(count);
        } else if (genreId != null && year == null) {
            return filmDao.getPopularFilmsWithGenre(count, genreId);
        } else if (genreId == null) {
            return filmDao.getPopularFilmsWithYear(count, year);
        }
        return filmDao.getPopularFilmsWithGenreAndYear(count, genreId, year);
    }

    public Film create(Film film) {
        validateFilm(film);
        film = filmDao.create(film);
        log.info("Фильм {} (id={}) успешно создан", film.getName(), film.getId());
        return film;
    }

    public Film save(Film film) {
        validateFilm(film);
        return filmDao.save(film);
    }

    public Collection<Film> getAll() {
        return filmDao.getAll();
    }


    public Collection<Film> getFilmsByIds(Collection<Integer> filmIds) {
        return filmDao.getFilmsByIds(filmIds);
    }

    public Collection<Film> searchFilms(String query, List<String> by) {
        return filmDao.loadFoundFilms(query, by);
    }

    public Film getFilmById(int id) {
        return filmDao.getFilmById(id);
    }

    private void validateFilm(Film film) {
        if (!film.getReleaseDate().isAfter(minFilmReleaseDate)) {
            throw new FilmBadReleaseDateException(String.format("Дата релиза должна быть позже %s", minFilmReleaseDate));
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmDao.getCommonFilms(userDao.getUserById(userId).getId(),
                userDao.getUserById(friendId).getId());
    }

    public void deleteUserById(int id) {
        filmDao.deleteUserById(id);
    }
}
