package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User add(User user) {
        checkUserName(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        checkUserName(user);
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getById(Integer id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует"));
    }

    public void addFriend(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        getById(idUser).addFriend(idFriend);
        getById(idFriend).addFriend(idUser);
        log.info("Пользователи с id {} и {} добавились друг другу в друзья", idUser, idFriend);
    }

    public void deleteFriend(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        getById(idUser).deleteFriend(idFriend);
        getById(idFriend).deleteFriend(idUser);
        log.info("Пользователи с id {} и {} удалились из друзей друг у друга", idUser, idFriend);
    }

    public List<User> getUserFriends(Integer idUser) {
        return getById(idUser).getFriends().stream().map(this::getById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        Set<Integer> friends = getById(idFriend).getFriends();
        List<User> commonFriend = getById(idUser).getFriends().stream()
                .filter(friends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
        log.info("У пользователей с id {} и {},  найдено {} общих друзей", idUser, idFriend, commonFriend.size());
        return commonFriend;
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkUserAndFriendId(Integer user, Integer friend) {
        if (user < 1 || friend < 1) {
            throw new NotFoundException("Id должны содержать числа больше нуля");
        }
    }
}
