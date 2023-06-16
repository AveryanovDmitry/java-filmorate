package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingDao;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {

    private final RatingDao ratingDao;

    public Rating create(Rating rating) {
        rating = ratingDao.create(rating);
        log.info("Возрастной рейтинг {} c id {} был успешно добавлен", rating.getName(), rating.getId());
        return rating;
    }

    public Rating getRatingById(int id) {
        return ratingDao.getRatingById(id);
    }

    public Collection<Rating> getAll() {
        return ratingDao.getAll();
    }
}
