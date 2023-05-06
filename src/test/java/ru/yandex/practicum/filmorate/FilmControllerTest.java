package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {
    private static FilmController filmController;
    private static Film film;
    private Validator validator;

    @BeforeEach
    void init() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void checkAddFilmTest() {
        filmController.add(film);
        film.setName("aaa");
        filmController.add(film);
        film.setName("bbb");
        filmController.add(film);
        film.setName("ccc");
        filmController.add(film);
        List<Film> films = filmController.getFilms();

        assertEquals(4, films.size(), "Количество фильмов не совпадает");

        assertEquals(film, films.get(films.size() - 1), "Фильм добавлен с ошибками");
    }

    @Test
    void checkNameTest() {
        film.setName(null);
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка имени фильма с null");

        film.setName("");
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка имени фильма с null");

        film.setName("   ");
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка имени фильма с null");
    }

    @Test
    void check200CharactersTest() {
        film.setDescription(null);
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка описания с null");

        film.setDescription(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        assertEquals(0, validator.validate(film).size(), "Неправильная обработка описания с 199");

        film.setDescription(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        assertEquals(0, validator.validate(film).size(), "Неправильная обработка описания с 200");

        film.setDescription(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                        "1");
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка описания с длинной 201");
    }

    @Test
    void checkReleaseDateTest() {
        final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
        final LocalDate LESS_THEN_MIN_RELEASE_DATE = LocalDate.of(1895, 12, 27);
        final LocalDate RIGHT_RELEASE_DATE = LocalDate.of(1895, 12, 29);

        film.setReleaseDate(null);
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка даты релиза с null");

        film.setReleaseDate(RIGHT_RELEASE_DATE);
        assertEquals(0, validator.validate(film).size(), "Неправильная обработка даты релиза");

        film.setReleaseDate(MIN_RELEASE_DATE);
        assertEquals(0, validator.validate(film).size(), "Неправильная обработка даты релиза");

        film.setReleaseDate(LESS_THEN_MIN_RELEASE_DATE);
        assertThrows(MyValidationExeption.class, () -> filmController.add(film),
                "дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    void checkNegativeDuration() {
        film.setDuration(null);
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка длительности с null");

        film.setDuration(1);
        assertEquals(0, validator.validate(film).size(), "Неправильная обработка длительности");

        film.setDuration(0);
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка длительности");

        film.setDuration(-1);
        assertEquals(1, validator.validate(film).size(), "Неправильная обработка длительности");
    }
}