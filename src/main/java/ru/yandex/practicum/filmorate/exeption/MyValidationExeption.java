package ru.yandex.practicum.filmorate.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MyValidationExeption extends ResponseStatusException {
    public MyValidationExeption(HttpStatus status, String message) {
        super(status, message);
    }
}
