package ru.yandex.practicum.filmorate.exception;

public class AlreadyFriendsException extends RuntimeException {
    public AlreadyFriendsException() {
        super("Пользователи уже друзья");
    }

    public AlreadyFriendsException(int userId, int friendId) {
        super(String.format("Пользователи с id %d и %d уже друзья", userId, friendId));
    }
}
