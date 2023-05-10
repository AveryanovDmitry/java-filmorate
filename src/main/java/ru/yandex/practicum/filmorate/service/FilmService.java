package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private static final LocalDate VALID_DATE = LocalDate.of(1895, 12, 28);
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film add(Film film) {
        checkReleaseDate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        checkReleaseDate(film);
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id);
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        filmStorage.getById(filmId).addLike(userId);
        log.info("добавили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        validateIsPositive(userId);
        validateIsPositive(filmId);
        filmStorage.getById(filmId).deleteLike(userId);
        log.info("удалили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(VALID_DATE)) {
            throw new MyValidationExeption(HttpStatus.BAD_REQUEST,
                    "дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private void validateIsPositive(Integer userId) {
        if (userId < 1) {
            throw new MyValidationExeption(HttpStatus.NOT_FOUND, "id пользователя должно быть положительным числом.");
        }
    }
}
