package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsStorageDateBase implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    private final UserMapper userMapper;

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

    private boolean findRequestsFriendship(Integer firstId, Integer secondId) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM FRIENDS\n" +
                "WHERE (first_user_id = %d OR second_user_id = %d)" +
                " AND (first_user_id = %d OR second_user_id = %d)", firstId, firstId, secondId, secondId);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    @Override
    public List<User> findAllFriends(Integer idUser) {
        String sqlQuery = String.format("SELECT * FROM USERS WHERE id IN (" +
                "SELECT second_user_id FROM FRIENDS WHERE first_user_id = %d)", idUser);
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public List<User> findCommonFriends(Integer idUser1, Integer idUser2) {
        String sqlQuery = String.format("SELECT * FROM USERS WHERE id IN (" +
                "SELECT f1.second_user_id " +
                "FROM FRIENDS as f1 " +
                "INNER JOIN FRIENDS as f2 ON f1.second_user_id = f2.second_user_id " +
                "WHERE f1.first_user_id = %d AND f2.first_user_id = %d)", idUser1, idUser2);
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public boolean deleteFriends(Integer idUser, Integer idFriend) {
        String sqlQuery = String.format("DELETE FROM FRIENDS WHERE first_user_id = %d AND second_user_id = %d",
                idUser, idFriend);
        return jdbcTemplate.update(sqlQuery) > 0;
    }
}
