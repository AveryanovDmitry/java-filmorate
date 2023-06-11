package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class,
            GenreNotFoundException.class, RatingNotFoundException.class, ReviewNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound(final RuntimeException e) {
        log.info(e.getMessage());
        return new ErrorResponse("Объект не найден", e.getMessage());
    }

    @ExceptionHandler({AlreadyFriendsException.class, FriendshipAcceptionException.class,
            FriendshipRequestAlreadyExist.class, DataIntegrityViolationException.class, AlreadyLikedException.class})

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFriendshipError(final RuntimeException e) {
        log.info(e.getMessage());
        return new ErrorResponse("Ошибка добавления", e.getMessage());
    }

    @ExceptionHandler({FilmBadReleaseDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFilmBadReleaseDateError(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Неверная дата", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationError(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.info("Ошибка сервера {}", e.getClass().getName());
        e.printStackTrace();
        return new ErrorResponse("Ошибка сервера", e.getMessage());
    }
}
