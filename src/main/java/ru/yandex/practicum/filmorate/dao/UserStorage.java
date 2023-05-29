package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(User user);

    List<User> getUsers();

    Optional<User> getById(Integer id);

    boolean addRequestsFriendship(Integer idUser, Integer idFriend);

    boolean deleteFriends(Integer idUser, Integer idFriend);

    List<User> findAllFriends(Integer idUser);

    List<User> findCommonFriends(Integer idUser1, Integer idUser2);
}
