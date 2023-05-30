package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    boolean addRequestsFriendship(Integer sender, Integer recipient);

    boolean deleteFriends(Integer idUser, Integer idFriend);

    List<User> findAllFriends(Integer idUser);

    List<User> findCommonFriends(Integer idUser1, Integer idUser2);
}
