package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDaoImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoImplTests {
    private final GenreDaoImpl genreDaoImpl;

    @Test
    public void testOneGenreGetById() {
        Genre comedy = genreDaoImpl.getGenreById(1);
        assertEquals("Комедия", comedy.getName());
    }

    @Test
    public void testOneUserCreate() {
        Collection<Genre> genres = genreDaoImpl.getAll();
        assertEquals(6, genres.size());
    }
}
