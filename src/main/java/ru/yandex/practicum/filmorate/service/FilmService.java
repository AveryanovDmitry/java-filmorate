package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate VALID_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;

    public Film add(Film film) {
        checkReleaseDate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        checkId(film.getId());
        checkReleaseDate(film);
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильма с id " + id + " не существует"));
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        filmStorage.addLike(filmId, userId);
        log.info("добавили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        validateIsPositive(userId);
        validateIsPositive(filmId);
        filmStorage.deleteLike(filmId, userId);
        log.info("удалили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.mostPopulars(count);
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(VALID_DATE)) {
            throw new MyValidationExeption(HttpStatus.BAD_REQUEST,
                    "дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private void validateIsPositive(Integer userId) {
        if (userId < 1) {
            throw new NotFoundException("id должно быть положительным числом.");
        }
    }

    private Film checkId(Integer id) {
        if (id > 0) {
            return filmStorage.getById(id).orElseThrow(() -> new NotFoundException("Фильма с таким id не существует"));
        } else {
            throw new NotFoundException("Проверьте id пользователя");
        }
    }
}
