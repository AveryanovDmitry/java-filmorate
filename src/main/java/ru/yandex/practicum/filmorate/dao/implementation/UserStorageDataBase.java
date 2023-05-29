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
    private static final UserMapper USER_MAPPER = new UserMapper();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageDataBase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        jdbcTemplate.update("INSERT INTO USERS(email, login, name, birthday) VALUES(?,?,?,?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        log.info("Пользователь добавлен: {}", user);
        return jdbcTemplate.query("SELECT * FROM USERS WHERE email = ? and name = ? and birthday = ? and login = ?",
                USER_MAPPER, user.getEmail(), user.getName(),
                user.getBirthday(), user.getLogin()).get(0);
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
        return jdbcTemplate.query("SELECT * FROM USERS", USER_MAPPER);
    }

    @Override
    public Optional<User> getById(Integer id) {
        List<User> user = jdbcTemplate.query("SELECT * FROM USERS WHERE id = ?", USER_MAPPER, id);
        if (!user.isEmpty()) {
            return Optional.of(user.get(0));
        }
        return Optional.empty();
    }

    @Override
    public boolean addRequestsFriendship(Integer sender, Integer recipient) {
        if (!findRequestsFriendship(sender, recipient)) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("first_user_id", sender);
            map.put("second_user_id", recipient);
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FRIENDS")
                    .usingColumns("first_user_id", "second_user_id");
            return simpleJdbcInsert.execute(map) == 1;
        }
        return false;
    }

    @Override
    public List<User> findAllFriends(Integer idUser) {
        String sqlQuery = String.format("SELECT * FROM USERS WHERE id IN (" +
                "SELECT second_user_id FROM FRIENDS WHERE first_user_id = %d)", idUser);
        return jdbcTemplate.query(sqlQuery, USER_MAPPER);
    }

    @Override
    public List<User> findCommonFriends(Integer idUser1, Integer idUser2) {
        String sqlQuery = String.format("SELECT * FROM USERS WHERE id IN (" +
                "SELECT f1.second_user_id " +
                "FROM FRIENDS as f1 " +
                "INNER JOIN FRIENDS as f2 ON f1.second_user_id = f2.second_user_id " +
                "WHERE f1.first_user_id = %d AND f2.first_user_id = %d)", idUser1, idUser2);
        return jdbcTemplate.query(sqlQuery, USER_MAPPER);
    }

    @Override
    public boolean deleteFriends(Integer idUser, Integer idFriend) {
        String sqlQuery = String.format("DELETE FROM FRIENDS WHERE first_user_id = %d AND second_user_id = %d",
                idUser, idFriend);
        return jdbcTemplate.update(sqlQuery) > 0;
    }

    private boolean findRequestsFriendship(Integer firstId, Integer secondId) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM FRIENDS\n" +
                "WHERE (first_user_id = %d OR second_user_id = %d)" +
                " AND (first_user_id = %d OR second_user_id = %d)", firstId, firstId, secondId, secondId);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    private User checkId(Integer id) {
        if (id > 0) {
            return getById(id).orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
        } else {
            throw new NotFoundException("Проверьте id пользователя");
        }
    }
}
