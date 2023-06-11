package ru.yandex.practicum.filmorate.exception;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException() {
        super("Возрастной рейтинг не найден");
    }

    public RatingNotFoundException(int id) {
        super(String.format("Возрастной рейтинг c id - %d не найден", id));
    }
}
