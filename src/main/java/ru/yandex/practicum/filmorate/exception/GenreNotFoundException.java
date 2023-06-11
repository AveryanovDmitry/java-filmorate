package ru.yandex.practicum.filmorate.exception;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException() {
        super("Жанр не найден");
    }

    public GenreNotFoundException(int id) {
        super(String.format("Жанр c id - %d не найден", id));
    }
}
