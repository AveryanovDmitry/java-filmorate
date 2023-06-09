package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.film.FilmDao;
import ru.yandex.practicum.filmorate.storage.review.ReviewDao;
import ru.yandex.practicum.filmorate.storage.user.UserDao;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final FilmDao filmDao;

    private final FeedDao feedDao;

    public Review create(Review review) {
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());

        Review reviewSave = reviewDao.save(review);
        feedDao.addFeedList(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);
        log.info("Отзыв (id={}) успешно создан", reviewSave.getReviewId());
        return reviewSave;
    }

    public Review update(Review review) {
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());

        Review reviewSave = reviewDao.update(review);
        feedDao.addFeedList(reviewSave.getUserId(), reviewSave.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        log.info("Отзыв (id={}) успешно обновлён", reviewSave.getReviewId());
        return reviewSave;
    }

    public Review get(int id) {
        return reviewDao.load(id);
    }

    public void delete(int id) {
        feedDao.addFeedList(get(id).getUserId(), get(id).getReviewId(), EventType.REVIEW, Operation.REMOVE);
        reviewDao.delete(id);
        log.info("Отзыв (id={}) успешно удалён", id);
    }

    public List<Review> getAll(Integer filmId, int count) {
        if (filmId != null) filmDao.getFilmById(filmId);
        return reviewDao.loadAll(filmId, count);
    }

    public void addLike(int id, int userId, boolean isLike) {
        userDao.getUserById(userId);

        reviewDao.saveLike(isLike ? 1 : -1, id);
        log.info("Лайк/дизлайк к отзыву (id={}) успешно сохранён", id);
    }
}
