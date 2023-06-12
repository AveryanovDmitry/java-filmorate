package ru.yandex.practicum.filmorate.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super("Отзыв не найден");
    }

    public ReviewNotFoundException(int id) {
        super(String.format("Отзыв c id - %d не найден", id));
    }
}
