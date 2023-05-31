package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;

import java.util.*;

@Component
@Slf4j
public class UserStorageDataBase implements UserStorage {
    private final UserMapper userMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageDataBase(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("USERS")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> userInMap = Map.of("email", user.getEmail(), "login", user.getLogin(),
                "name", user.getName(), "birthday", user.getBirthday());
        int idUserInBD = simpleJdbcInsert.executeAndReturnKey(userInMap).intValue();
        user.setId(idUserInBD);

        return user;
    }

    @Override
    public User update(User user) {
        checkId(user.getId());
        jdbcTemplate.update("UPDATE USERS SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь обновлён по id: {}", user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", userMapper);
    }

    @Override
    public Optional<User> getById(Integer id) {
        List<User> user = jdbcTemplate.query("SELECT * FROM USERS WHERE id = ?", userMapper, id);
        if (!user.isEmpty()) {
            return Optional.of(user.get(0));
        }
        return Optional.empty();
    }

    public void checkId(Integer id) {
        String sqlQuery = String.format("select exists(SELECT * FROM USERS WHERE id = %d)", id);
        if (!Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class))) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }
}
