package ru.yandex.practicum.filmorate.exception;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException() {
        super("Лайк уже поставлен");
    }

    public AlreadyLikedException(int userId, int friendId) {
        super(String.format("Лайк фильму с id %d пользователем %d уже поставлен", userId, friendId));
    }
}
