package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTests {

    UserController userController;

    @BeforeEach
    void makeUserController() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        userController = new UserController(new UserService(userStorage, null), new FilmService(filmStorage, userStorage, null, null));
    }

    @Test
    void shouldCreateNewUser() {
        User user = User.builder()
                .id(1)
                .name("Руслан")
                .email("rudusaev@yandex.ru")
                .login("rooslean")
                .birthday(LocalDate.of(1999, 3, 18))
                .build();

        assertEquals(user, userController.create(user));
    }
}
