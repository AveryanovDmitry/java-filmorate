package ru.yandex.practicum.filmorate.exception;

public class FilmBadReleaseDateException extends RuntimeException {
    public FilmBadReleaseDateException() {
        super("Фильм содержит невалидные данные");
    }

    public FilmBadReleaseDateException(String msg) {
        super(msg);
    }
}
