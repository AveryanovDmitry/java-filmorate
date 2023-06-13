package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    private final FeedDao feedDao;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FeedDao feedDao) {
        this.userStorage = userStorage;
        this.feedDao = feedDao;
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userStorage.create(user);
        log.info("Пользователь с идентификатором {} и логином {} был создан", user.getId(), user.getLogin());
        return user;
    }

    public User save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userStorage.save(user);
        log.info("Данные пользователя {} (id={}) успешно обновлены", user.getLogin(), user.getId());
        return user;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userStorage.addFriend(userId, friendId);
        feedDao.addFeedList(userId, friendId, EventType.FRIEND, Operation.ADD);
        log.info("Создан запрос дружбы между пользователями {} и {}", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        feedDao.addFeedList(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public Collection<User> getFriendsList(int userId) {
        User user = userStorage.getUserById(userId);
        return userStorage.getFriendsList(user);
    }

    public Collection<User> getCommonFriendsList(int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);
        return userStorage.getCommonFriendsList(user, other);
    }

    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
    }
}
