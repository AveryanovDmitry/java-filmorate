package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate VALID_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;

    public Film add(Film film) {
        checkReleaseDate(film);
        Film filmWithId = filmStorage.add(film);

        if (!film.getGenres().isEmpty()) {
            genreStorage.add(film.getGenres(), filmWithId.getId());
        }
        return filmWithId;
    }

    public Film update(Film film) {
        filmStorage.checkId(film.getId());
        checkReleaseDate(film);
        filmStorage.update(film);
        genreStorage.updateGenre(film.getGenres(), film.getId());

        return film;
    }

    public List<Film> getFilms() {
        return addGenresForFilms(filmStorage.getFilms());
    }

    public Film getById(Integer id) {
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильма с id " + id + " не существует"));
        film.setGenres(genreStorage.getFilmGenresByFilmId(id));
        return film;
    }

    public void addLikeFilm(Integer filmId, Integer userId) {
        likesStorage.addLike(filmId, userId);
        log.info("добавили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public void deleteLikeFilm(Integer filmId, Integer userId) {
        validateIsPositive(userId);
        validateIsPositive(filmId);
        likesStorage.deleteLike(filmId, userId);
        log.info("удалили like пользователя с id {} фильму с id {}", userId, filmId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return addGenresForFilms(filmStorage.mostPopulars(count));
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

    private List<Film> addGenresForFilms(List<Film> films) {
        if (!films.isEmpty()) {
            List<Integer> idFilms = films.stream().map(Film::getId).collect(Collectors.toList());
            Map<Integer, LinkedHashSet<Genre>> mapIdFilmGenres = genreStorage.getGenresListFilmsId(idFilms);
            for (Film film : films) {
                if (mapIdFilmGenres.containsKey(film.getId())) {
                    film.setGenres(mapIdFilmGenres.get(film.getId()));
                }
            }
        }
        return films;
    }
}
