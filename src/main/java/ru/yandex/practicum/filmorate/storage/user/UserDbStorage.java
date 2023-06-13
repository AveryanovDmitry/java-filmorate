package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.FriendshipAcceptionException;
import ru.yandex.practicum.filmorate.exception.FriendshipRequestAlreadyExist;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Component("UserDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id");
        int id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User save(User user) {
        String sqlQuery = "UPDATE user SET " +
                "email = ?, login = ?, name = ? , birthday = ?" +
                "where user_id = ?";
        int result = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (result == 0) {
            throw new UserNotFoundException(user.getId());
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT user_id, email, login, name, birthday FROM user";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "SELECT user_id, email, login, name, birthday " +
                "FROM user WHERE user_id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        SqlRowSet friendRequest = findFriendRequest(userId, friendId);
        if (friendRequest.next()) {
            int followingUserId = friendRequest.getInt("following_user_id");
            int followedUserId = friendRequest.getInt("followed_user_id");
            int friendshipId = friendRequest.getInt("id");
            boolean accepted = friendRequest.getBoolean("accepted");
            if (accepted) {
                throw new AlreadyFriendsException(followingUserId, followedUserId);
            }
            if (userId == followingUserId) {
                throw new FriendshipRequestAlreadyExist(userId, friendId);
            }
            if (userId == followedUserId) {
                String sqlQuery = "UPDATE friendship " +
                        "SET accepted = ? " +
                        "WHERE friendship_id = ?";
                int result = jdbcTemplate.update(sqlQuery, true, friendshipId);
                if (result == 0) {
                    throw new FriendshipAcceptionException(userId, friendId);
                }
            }
        } else {
            String sqlQuery = "INSERT INTO friendship(following_user_id, followed_user_id) " +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friendship " +
                "WHERE following_user_id = ? " +
                "AND followed_user_id = ? " +
                "OR following_user_id = ? " +
                "AND followed_user_id = ? ";
        jdbcTemplate.update(sqlQuery, userId, friendId, friendId, userId);
    }

    @Override
    public Collection<User> getFriendsList(User user) {
        String sqlQuery = "SELECT user_id, email, login, name, birthday " +
                "FROM user " +
                "WHERE user_id IN (SELECT followed_user_id " +
                "FROM friendship " +
                "WHERE following_user_id = ? " +
                "UNION " +
                "SELECT following_user_id " +
                "FROM friendship " +
                "WHERE followed_user_id = ? " +
                "AND accepted = ?)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user.getId(), user.getId(), true);
    }

    @Override
    public Collection<User> getCommonFriendsList(User user, User other) {
        String sqlQuery = "SELECT user_id, email, login, name, birthday " +
                "FROM user " +
                "WHERE user_id IN (SELECT user_friends.user_id " +
                "FROM (SELECT followed_user_id user_id " +
                "FROM friendship " +
                "WHERE following_user_id = ? " +
                "UNION " +
                "SELECT following_user_id " +
                "FROM friendship " +
                "WHERE followed_user_id = ? " +
                "AND accepted = ?) user_friends JOIN (SELECT followed_user_id user_id " +
                "FROM friendship " +
                "WHERE following_user_id = ? " +
                "UNION " +
                "SELECT following_user_id " +
                "FROM friendship " +
                "WHERE followed_user_id = ? " +
                "AND accepted = ?) other_friends ON user_friends.user_id=other_friends.user_id)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser,
                user.getId(), user.getId(), true, other.getId(), other.getId(), true);

    }

    private SqlRowSet findFriendRequest(int userId, int friendId) {
        String sqlQuery = "SELECT * " +
                "FROM friendship " +
                "WHERE following_user_id = ? " +
                "AND followed_user_id = ? " +
                "OR following_user_id = ? " +
                "AND followed_user_id = ? ";
        return jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId, friendId, userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getObject("birthday", LocalDate.class))
                .build();
    }

    @Override
    public void deleteUserById(int id) {
        String sqlQuery = "DELETE FROM user WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }
}
