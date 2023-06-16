package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedDao;
import ru.yandex.practicum.filmorate.storage.user.UserDao;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    private final FeedDao feedDao;

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userDao.create(user);
        log.info("Пользователь с идентификатором {} и логином {} был создан", user.getId(), user.getLogin());
        return user;
    }

    public User save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userDao.save(user);
        log.info("Данные пользователя {} (id={}) успешно обновлены", user.getLogin(), user.getId());
        return user;
    }

    public Collection<User> getAll() {
        return userDao.getAll();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        userDao.getUserById(userId);
        userDao.getUserById(friendId);
        userDao.addFriend(userId, friendId);
        feedDao.addFeedList(userId, friendId, EventType.FRIEND, Operation.ADD);
        log.info("Создан запрос дружбы между пользователями {} и {}", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userDao.deleteFriend(userId, friendId);
        feedDao.addFeedList(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public Collection<User> getFriendsList(int userId) {
        User user = userDao.getUserById(userId);
        return userDao.getFriendsList(user);
    }

    public Collection<User> getCommonFriendsList(int userId, int otherId) {
        User user = userDao.getUserById(userId);
        User other = userDao.getUserById(otherId);
        return userDao.getCommonFriendsList(user, other);
    }

    public Collection<Integer> getUserFilmIdsRecommendations(int userId) {
        return userDao.getUserFilmIdsRecommendations(userId);
    }

    public void deleteUserById(int id) {
        userDao.deleteUserById(id);
    }
}
