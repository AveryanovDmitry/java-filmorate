package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public User create(User user) {
        user.setId(id);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(id, user);
        id++;
        log.info("Пользователь {} (id={}) успешно создан", user.getLogin(), user.getId());
        return user;
    }

    public User save(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        //Сохранение списка друзей при обновлении
        user.setFriends(getUserById(user.getId()).getFriends());
        users.put(user.getId(), user);
        log.info("Данные пользователя {} (id={}) успешно обновлены", user.getLogin(), user.getId());
        return user;
    }

    public Collection<User> getAll() {
        return users.values();
    }

    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) throw new UserNotFoundException(id);
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(user);
        friend.getFriends().add(friend);
        save(user);
        save(friend);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().remove(user);
        friend.getFriends().remove(user);
        save(user);
        save(friend);
    }

    @Override
    public Collection<User> getFriendsList(User user) {
        return user.getFriends();
    }

    @Override
    public Collection<User> getCommonFriendsList(User user, User other) {
        Collection<User> userFriends = user.getFriends();
        Collection<User> otherFriends = other.getFriends();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Integer> getUserFilmIdsRecommendations(int userId) {
        return null;
    }
  
    @Override
    public void deleteUserById(int id) {
  
    }
}
