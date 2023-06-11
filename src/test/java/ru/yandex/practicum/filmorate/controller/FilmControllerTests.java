package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmBadReleaseDateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FilmControllerTests {

    FilmController filmController;

    @BeforeEach
    void makeFilmController() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        filmController = new FilmController(new FilmService(filmStorage, userStorage));
    }


    @Test
    void shouldCreateNewFilm() {
        Film film = Film.builder()
                .name("Джентльмены")
                .description(String.format("Один ушлый американец ещё со студенческих лет приторговывал наркотиками,%s",
                        " а теперь придумал схему нелегального обогащения."))
                .releaseDate(LocalDate.of(2020, 2, 13))
                .duration(113).build();

        Film addedFilm = filmController.create(film);
        assertEquals(film, addedFilm);
    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder()
                .name("Джентльмены")
                .description(String.format("Один ушлый американец ещё со студенческих лет приторговывал наркотиками,%s",
                        " а теперь придумал схему нелегального обогащения."))
                .releaseDate(LocalDate.of(2020, 2, 13))
                .duration(113).build();
        Film addedFilm = filmController.create(film);
        assertEquals(film, addedFilm);
    }

    @Test
    void shouldCreateFilmWhenDescriptionLengthIs199() {
        Film film = Film.builder()
                .name("Джентльмены")
                .description(String.format("Один ушлый американец ещё со студенческих лет приторговывал наркотиками, %s%s",
                        "а теперь придумал схему нелегального обогащения с использованием поместий обедневшей ",
                        "английской аристократии и очень неплохо н"))
                .releaseDate(LocalDate.of(2020, 2, 13))
                .duration(113).build();

        assertEquals(film, filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDateEqualsMinDate() {
        Film film = Film.builder()
                .name("Джентльмены")
                .description(String.format("Один ушлый американец ещё со студенческих лет приторговывал наркотиками,%s",
                        " а теперь придумал схему нелегального обогащения."))
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(113).build();

        final FilmBadReleaseDateException exception = assertThrows(
                FilmBadReleaseDateException.class,
                () -> filmController.create(film));

        assertEquals("Дата релиза должна быть позже 1895-12-28", exception.getMessage());
    }
}
