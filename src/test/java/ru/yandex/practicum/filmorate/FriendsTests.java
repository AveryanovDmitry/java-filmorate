package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FriendsTests {
    private final FriendsStorage friendsStorage;
    private final UserStorage userStorage;

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
    void testAddRequestsFriendship() {
        assertTrue(friendsStorage.addRequestsFriendship(1, 2), "Запрос на дружбу не отправлен");
        assertFalse(friendsStorage.addRequestsFriendship(1, 2), "Запрос на дружбу не должен быть отправлен");
    }

    @Test
    void testDeleteFriends() {
        friendsStorage.addRequestsFriendship(1, 2);
        assertTrue(friendsStorage.deleteFriends(1, 2), "Запрос на дружбу не удален");
        assertFalse(friendsStorage.deleteFriends(1, 2), "Запрос на дружбу не должен быть удален");
    }

    @Test
    void testFindAllFriends() {
        friendsStorage.addRequestsFriendship(1, 2);
        List<User> friends = friendsStorage.findAllFriends(1);
        assertEquals(1, friends.size(), "В списке друзей должен быть 1 друг");
        assertEquals(2, friends.get(0).getId(), "Значение ID друга должно равнятся 2");
        List<User> listFriendIdTwo = friendsStorage.findAllFriends(2);
        assertEquals(0, listFriendIdTwo.size(), "Список друзей должен быть пуст");
    }
}