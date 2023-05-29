package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.implementation.GenreStorageDataBase;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreTests {

    final GenreStorageDataBase genreDbStorage;

    @Test
    void testFindNameGenre() {
        LinkedList<String> nameGenre = new LinkedList<>();
        nameGenre.add("Комедия");
        nameGenre.add("Драма");
        nameGenre.add("Мультфильм");
        nameGenre.add("Триллер");
        nameGenre.add("Документальный");
        nameGenre.add("Боевик");
        for (int i = 0; i < nameGenre.size(); i++) {
            assertEquals(genreDbStorage.findById(i + 1), nameGenre.get(i), "Название не соответствует");
        }
    }

    @Test
    void testFindAll() {
        assertEquals(6, genreDbStorage.findAll().size(), "Размер не соответствует");
    }
}