package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.implementation.FilmStorageDataBase;
import ru.yandex.practicum.filmorate.dao.implementation.UserStorageDataBase;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmTests {

    final FilmStorageDataBase filmDbStorage;
    final GenreStorage genreDbStorage;
    final UserStorageDataBase userStorage;

    @BeforeEach
    void createdFilmForDB() {
        if (filmDbStorage.getFilms().size() != 2) {
            List<Genre> genres = new ArrayList<>();
            genres.add(new Genre(2, genreDbStorage.findById(2)));
            Film film = new Film("Film1", "Description film1", LocalDate.parse("1999-01-01"),
                    80, new Mpa(1, "G"), genres, 4);
            filmDbStorage.add(film);
            Film filmNext = new Film("Film2", "Description film2", LocalDate.parse("2020-01-01"),
                    70, new Mpa(2, "PG"), genres, 0);
            filmDbStorage.add(filmNext);
        }
        if (userStorage.getUsers().size() != 2) {
            userStorage.add(new User("user1", "User1", "user1@yandex.ru",
                    LocalDate.parse("1995-01-01")));
            userStorage.add(new User("user2", "User2", "user2@yandex.ru",
                    LocalDate.parse("2005-01-01")));
        }
    }

    @Test
    void testAddFilm() {
        checkFindFilmById(1);
        checkFindFilmById(2);
    }

    @Test
    void testFindFilm() {
        checkFindFilmById(1);
    }

    @Test
    void testFindAll() {
        List<Film> current = filmDbStorage.getFilms();
        Assertions.assertEquals(2, current.size(), "Некорректное количество фильмов");
    }

    @Test
    void testAddLikeFilm() {
        assertTrue(filmDbStorage.addLike(1, 1), "Лайк не добавлен");
        filmDbStorage.deleteLike(1, 1);
    }

    @Test
    void testDeleteLike() {
        filmDbStorage.addLike(1, 1);
        assertTrue(filmDbStorage.deleteLike(1, 1), "Лайк не удален");
    }

    void checkFindFilmById(Integer idFilm) {
        Optional<Film> filmOptional = filmDbStorage.getById(idFilm);
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> assertThat(film)
                .hasFieldOrPropertyWithValue("id", idFilm));
    }
}