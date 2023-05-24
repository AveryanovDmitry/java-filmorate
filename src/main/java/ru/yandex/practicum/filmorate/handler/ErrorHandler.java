package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> exceptionHandler(ConstraintViolationException e) {
        log.debug("400 Неправильный запрос {}", (Object) e.getStackTrace());
        return Map.of("Неправильный запрос.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> exceptionHandler(Throwable e) {
        log.debug("500 Произошла непредвиденная ошибка {}", (Object) e.getStackTrace());
        return Map.of("Непредвиденная ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> exceptionHandler(MethodArgumentNotValidException e) {
        log.debug("400 Неправильный запрос {}", (Object) e.getStackTrace());
        return Map.of("Неправильный запрос.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> exceptionHandler(NotFoundException e) {
        log.debug("404 По этому запросу ничего не найдено {}", (Object) e.getStackTrace());
        return Map.of("По этому запросу ничего не найдено {}", e.getMessage());
    }
}