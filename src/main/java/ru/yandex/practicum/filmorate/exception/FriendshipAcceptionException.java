package ru.yandex.practicum.filmorate.exception;

public class FriendshipAcceptionException extends RuntimeException {
    public FriendshipAcceptionException() {
        super("Не удалось подтвердить запрос в друзья");
    }

    public FriendshipAcceptionException(int userId, int friendId) {
        super(String.format("Не удалось подтвердить запрос в друзья пользователей с id %d и %d", userId, friendId));
    }
}
