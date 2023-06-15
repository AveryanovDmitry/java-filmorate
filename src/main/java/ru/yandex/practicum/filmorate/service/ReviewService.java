package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final FeedStorage feedStorage;

    public Review create(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());

        Review reviewSave = reviewDao.save(review);
        feedStorage.addFeedList(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);
        log.info("Отзыв (id={}) успешно создан", reviewSave.getReviewId());
        return reviewSave;
    }

    public Review update(Review review) {
        userStorage.getUserById(review.getUserId());
        filmStorage.getFilmById(review.getFilmId());

        Review reviewSave = reviewDao.update(review);
        feedStorage.addFeedList(reviewSave.getUserId(), reviewSave.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        log.info("Отзыв (id={}) успешно обновлён", reviewSave.getReviewId());
        return reviewSave;
    }

    public Review get(int id) {
        return reviewDao.load(id);
    }

    public void delete(int id) {
        feedStorage.addFeedList(get(id).getUserId(), get(id).getReviewId(), EventType.REVIEW, Operation.REMOVE);
        reviewDao.delete(id);
        log.info("Отзыв (id={}) успешно удалён", id);
    }

    public List<Review> getAll(Integer filmId, int count) {
        if (filmId != null) filmStorage.getFilmById(filmId);
        return reviewDao.loadAll(filmId, count);
    }

    public void addLike(int id, int userId, boolean isLike) {
        userStorage.getUserById(userId);

        reviewDao.saveLike(isLike ? 1 : -1, id);
        log.info("Лайк/дизлайк к отзыву (id={}) успешно сохранён", id);
    }
}
