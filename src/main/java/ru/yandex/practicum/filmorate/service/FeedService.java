package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FeedService {
    private final FeedDao feedDao;
    private final UserStorage userStorage;

    @Autowired
    public FeedService(FeedDao feedDao, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.feedDao = feedDao;
        this.userStorage = userStorage;
    }

    public List<Feed> getFeedList(int id) {
        userStorage.getUserById(id);
        return feedDao.getFeedList(id);
    }
}