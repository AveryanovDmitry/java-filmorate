package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbDao;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbDao userDbStorage;

    User createUser() {
        return userDbStorage.create(User.builder()
                .email("test@mail.com")
                .login("test")
                .name("Test")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());
    }

    @Test
    public void testCreateUserAndGetUserById() {
        User user = createUser();
        User userFromDb = userDbStorage.getUserById(user.getId());
        assertEquals(user.getId(), userFromDb.getId());
    }

    @Test
    public void testSave() {
        User user = createUser();
        user.setName("Beta tester");
        userDbStorage.save(user);
        User userFromDb = userDbStorage.getUserById(user.getId());
        assertEquals("Beta tester", userFromDb.getName());
    }

    @Test
    public void testGetFriendsList() {
        User user = createUser();
        User friend = createUser();
        userDbStorage.addFriend(user.getId(), friend.getId());
        Collection<User> friends = userDbStorage.getFriendsList(user);
        assertThat(friends.stream()
                .findFirst())
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", friend.getId()));
        userDbStorage.deleteFriend(user.getId(), friend.getId());
        friends = userDbStorage.getFriendsList(user);
        assertEquals(0, friends.size());
    }
}
