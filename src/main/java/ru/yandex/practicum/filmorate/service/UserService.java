package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public User add(User user) {
        checkUserName(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        userStorage.checkId(user.getId());
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
        getById(idUser);
        getById(idFriend);
        friendsStorage.addRequestsFriendship(idUser, idFriend);
    }

    public void deleteFriend(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        getById(idUser);
        getById(idFriend);
        if (!friendsStorage.deleteFriends(idUser, idFriend)) {
            throw new NotFoundException("Не удалось удалить пользователя из друзей");
        }
    }

    public List<User> getUserFriends(Integer idUser) {
        getById(idUser);
        return friendsStorage.findAllFriends(idUser);
    }

    public List<User> getCommonFriends(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        return friendsStorage.findCommonFriends(idUser, idFriend);
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
