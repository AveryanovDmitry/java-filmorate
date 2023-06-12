package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    Review save(Review review);

    Review update(Review review);

    Review load(int id);

    List<Review> loadAll(Integer filmId, int count);

    void delete(int id);

    void saveLike(int value, int id);

}
