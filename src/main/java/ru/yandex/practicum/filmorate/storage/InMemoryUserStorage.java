package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.MyValidationExeption;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public User add(User user) {
        user.setId(id);
        users.put(user.getId(), user);
        id++;
        log.info("Пользователь добавлен: {}", user);
        return users.get(user.getId());
    }

    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь обновлён по id: {}", user.getId());
            return users.get(user.getId());
        } else {
            log.info("Пользователя с таким id не существует: {}", user.getId());
            throw new MyValidationExeption(HttpStatus.NOT_FOUND, "Пользователя с таким id не существует");
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getById(Integer id) {
        checkId(id);
        return users.get(id);
    }

    private void checkId(Integer id) {
        if (!users.containsKey(id)) {
            log.info("Пользователя с таким id не существует: {}", id);
            throw new MyValidationExeption(HttpStatus.NOT_FOUND, "Пользователя с таким id не существует");
        }
    }
}
