package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    User createUser() {
        return userDbStorage.create(User.builder()
                .email("test@mail.com")
                .login("test")
                .name("Test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());
    }

    Film createFilm() {
        return filmDbStorage.create(Film.builder()
                .name("Форсаж 7")
                .description("Это последняя гонка")
                .releaseDate(LocalDate.of(2015, 4, 10))
                .mpa(Rating.builder().id(4).name("R").build())
                .build());
    }

    @Test
    public void testCreateFilmAndGetFilmById() {
        Film film = createFilm();
        Film filmFromDb = filmDbStorage.getFilmById(film.getId());
        assertEquals(film.getId(), filmFromDb.getId());
    }

    @Test
    public void testSave() {
        Film film = createFilm();
        film.setName("Форсаж 8");
        filmDbStorage.save(film);
        Film filmFromDb = filmDbStorage.getFilmById(film.getId());
        assertEquals("Форсаж 8", filmFromDb.getName());
    }

    @Test
    public void testGetPopularFilms() {
        Film fastSeven = createFilm();
        Film fastEight = createFilm();
        fastEight.setName("Форсаж 8");
        filmDbStorage.save(fastEight);
        User userOne = createUser();
        User userTwo = createUser();

        filmDbStorage.addLike(fastSeven.getId(), userOne.getId());
        filmDbStorage.addLike(fastSeven.getId(), userTwo.getId());
        filmDbStorage.addLike(fastEight.getId(), userOne.getId());

        Collection<Film> popularFilms = filmDbStorage.getPopularFilms(10);
        System.out.println(popularFilms);

        assertThat(popularFilms.stream()
                .findFirst())
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", fastSeven.getId()));

        filmDbStorage.deleteLike(fastSeven.getId(), userOne.getId());
        filmDbStorage.deleteLike(fastSeven.getId(), userTwo.getId());

        popularFilms = filmDbStorage.getPopularFilms(10);
        System.out.println(popularFilms);
        assertThat(popularFilms.stream()
                .findFirst())
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", fastEight.getId()));
    }
}
