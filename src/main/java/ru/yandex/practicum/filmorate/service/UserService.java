package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        checkUserName(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getById(Integer id) {
        return userStorage.getById(id);
    }

    public void addFriend(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        userStorage.getById(idUser).addFriend(idFriend);
        userStorage.getById(idFriend).addFriend(idUser);
        log.info("Пользователи с id {} и {} добавились друг другу в друзья", idUser, idFriend);
    }

    public void deleteFriend(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        userStorage.getById(idUser).deleteFriend(idFriend);
        userStorage.getById(idFriend).deleteFriend(idUser);
        log.info("Пользователи с id {} и {} удалились из друзей друг у друга", idUser, idFriend);
    }

    public List<User> getUserFriends(Integer idUser) {
        List<User> friends = new ArrayList<>();
        Set<Integer> setIdFriends = getById(idUser).getFriends();

        for (Integer friendId : setIdFriends) {
            friends.add(userStorage.getById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(Integer idUser, Integer idFriend) {
        checkUserAndFriendId(idUser, idFriend);
        List<User> commonFriend = new ArrayList<>();
        Set<Integer> friendsUser = getById(idUser).getFriends();
        Set<Integer> friendsFriend = getById(idFriend).getFriends();
        for (Integer id : friendsUser) {
            if (friendsFriend.contains(id)) {
                commonFriend.add(userStorage.getById(id));
            }
        }
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
            throw new MyValidationExeption(HttpStatus.BAD_REQUEST, "Id должны содержать числа больше нуля");
        }
    }
}
