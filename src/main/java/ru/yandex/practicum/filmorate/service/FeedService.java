package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.user.UserDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedDao feedDao;
    private final UserDao userDao;

    public List<Feed> getFeedList(int id) {
        userDao.getUserById(id);
        return feedDao.getFeedList(id);
    }
}