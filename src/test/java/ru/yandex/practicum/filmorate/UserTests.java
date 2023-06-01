package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.implementation.UserStorageDataBase;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserTests {

    private final UserStorageDataBase userStorage;
    private final FriendsStorage friendsStorage;

    @BeforeEach
    void createdUserForDB() {
        if (userStorage.getUsers().size() != 2) {
            userStorage.add(new User("user1@yandex.ru", "User1", "user1",
                    LocalDate.parse("2005-01-01")));
            userStorage.add(new User("user2@yandex.ru", "User2", "user2",
                    LocalDate.parse("1995-01-01")));
        }
        friendsStorage.deleteFriends(1, 2);
    }

    @Test
    void testCreatedUser() {
        checkFindUserById(1);
        checkFindUserById(2);
    }

    @Test
    void testFindAll() {
        List<User> currentList = userStorage.getUsers();
        assertEquals(2, currentList.size(), "Некорректное количество пользователей");
    }

    @Test
    void testUpgradeUser() {
        User updateUser = new User("update@yandex.ru",
                "updateUser",
                "UpdateName",
                LocalDate.parse("2000-10-10"));
        updateUser.setId(1);
        userStorage.update(updateUser);
        Optional<User> userStorageUser = userStorage.getById(1);
        Map<String, Object> mapForCheck = new HashMap<>();
        mapForCheck.put("id", updateUser.getId());
        mapForCheck.put("email", updateUser.getEmail());
        mapForCheck.put("login", updateUser.getLogin());
        mapForCheck.put("name", updateUser.getName());
        mapForCheck.put("birthday", updateUser.getBirthday());
        for (Map.Entry<String, Object> entry : mapForCheck.entrySet()) {
            assertThat(userStorageUser).isPresent().hasValueSatisfying(user -> assertThat(user)
                    .hasFieldOrPropertyWithValue(entry.getKey(), entry.getValue())
            );
        }
    }

    @Test
    void testFindUserById() {
        checkFindUserById(1);
    }

    void checkFindUserById(Integer idUser) {
        Optional<User> userStorageById = userStorage.getById(idUser);
        assertThat(userStorageById).isPresent().hasValueSatisfying(user -> assertThat(user)
                .hasFieldOrPropertyWithValue("id", idUser));
    }
}