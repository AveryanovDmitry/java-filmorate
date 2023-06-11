package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDaoImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingDaoImplTests {

    private final RatingDaoImpl ratingDaoImpl;

    @Test
    public void testGetRatingById() {
        Rating rating = ratingDaoImpl.getRatingById(1);
        assertEquals("G", rating.getName());
    }

    @Test
    public void testFindAllRatings() {
        Collection<Rating> ratings = ratingDaoImpl.getAll();
        assertEquals(5, ratings.size());
    }

}

