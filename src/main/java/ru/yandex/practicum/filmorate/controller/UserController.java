package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        checkUserName(user);
        user.setId(id);
        users.put(user.getId(), user);
        id++;
        log.info("Пользователь добавлен: {}", user);
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            checkUserName(user);
            users.put(user.getId(), user);
            log.info("Пользователь обновлён по id: {}", user.getId());
            return users.get(user.getId());
        } else {
            log.info("Пользователя с таким id не существует: {}", user.getId());
            throw new MyValidationExeption(HttpStatus.NOT_FOUND, "Пользователя с таким id не существует");
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkUserName(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
