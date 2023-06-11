package ru.yandex.practicum.filmorate.exception;

public class FriendshipRequestAlreadyExist extends RuntimeException {
    public FriendshipRequestAlreadyExist() {
        super("Запрос в друзья уже отправлен");
    }

    public FriendshipRequestAlreadyExist(int userId, int friendId) {
        super(String.format("Пользователь с id %d уже отправил запрос в друзья пользователю %d", userId, friendId));
    }
}
