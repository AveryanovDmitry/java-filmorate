package ru.yandex.practicum.filmorate.exception;

public class DirectorNotFoundException extends RuntimeException {
    public DirectorNotFoundException() {
        super("Режиссер не найден");
    }

    public DirectorNotFoundException(int id) {
        super(String.format("Режиссер c id - %d не найден", id));
    }
}
