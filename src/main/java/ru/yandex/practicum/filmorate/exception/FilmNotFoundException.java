package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException() {
        super("Фильм не найден");
    }

    public FilmNotFoundException(int id) {
        super(String.format("Фильм c id - %d не найден", id));
    }
}
