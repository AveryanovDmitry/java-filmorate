package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        getById(filmId).addLike(userId);
        log.info("добавили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        validateIsPositive(userId);
        validateIsPositive(filmId);
        getById(filmId).deleteLike(userId);
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
