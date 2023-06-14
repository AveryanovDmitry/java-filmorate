package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User save(User user);

    Collection<User> getAll();

    User getUserById(int id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    Collection<User> getFriendsList(User user);

    Collection<User> getCommonFriendsList(User user, User other);

    Collection<Integer> getUserFilmIdsRecommendations(int userId);
  
    void deleteUserById(int id);
}
