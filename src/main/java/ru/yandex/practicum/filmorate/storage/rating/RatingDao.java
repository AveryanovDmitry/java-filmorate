package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

public interface RatingDao {
    Rating create(Rating rating);

    Rating getRatingById(int id);

    Collection<Rating> getAll();
}
