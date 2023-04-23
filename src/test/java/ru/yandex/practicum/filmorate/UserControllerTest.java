package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    private static UserController userController;
    private static User user;
    private Validator validator;

    @BeforeEach
    void init() {
        userController = new UserController();
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void addUserTest() {
        userController.add(user);
        user.setLogin("aaa");
        userController.add(user);
        user.setLogin("bbb");
        userController.add(user);
        user.setLogin("ccc");
        userController.add(user);
        user.setLogin("ddd");
        List<User> users = userController.getUsers();

        assertEquals(4, users.size(), "Количество пользователей не совпадает");
        System.out.println(user.getId());
        assertEquals(user, users.get(user.getId() - 1), "Пользователь добавлен с ошибками");
    }

    @Test
    void checkEmailTest() {
        user.setEmail(null);
        assertEquals(1, validator.validate(user).size(), "Неверная обработка email с null");

        user.setEmail("");
        assertEquals(1, validator.validate(user).size(), "Неверная обработка email");

        user.setEmail("@");
        assertEquals(1, validator.validate(user).size(), "Неверная обработка email");


        user.setEmail("aa@.ru");
        assertEquals(1, validator.validate(user).size(), "Неверная обработка email");

        user.setEmail("yandex@yandex.ru");
        assertEquals(0, validator.validate(user).size(), "Неверная обработка email");
    }

    @Test
    void checkLoginTest() {
        user.setLogin(null);
        assertEquals(1, validator.validate(user).size(), "Неверная обработка логина");

        user.setLogin("                                               ");
        assertEquals(2, validator.validate(user).size(), "Неверная обработка логина");

        user.setLogin("aaa aaa");
        assertEquals(1, validator.validate(user).size(), "Неверная обработка логина");
    }

    @Test
    void checkNameTest() {
        user.setName(null);
        userController.add(user);
        assertEquals(user.getLogin(), userController.getUsers().get(0).getName(), "Имя не изменено на логин");

        user.setName("");
        userController.add(user);
        assertEquals(user.getLogin(), userController.getUsers().get(0).getName(), "Имя не изменено на логин");

        user.setName("                                          ");
        userController.add(user);
        assertEquals(user.getLogin(), userController.getUsers().get(0).getName(), "Имя не изменено на логин");
    }

    @Test
    void checkBirthdayTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertEquals(1, validator.validate(user).size(), "Неправильная обработка даты рождения");

        user.setBirthday(LocalDate.now().minusYears(1));
        assertEquals(0, validator.validate(user).size(), "Неправильная обработка даты рождения");
    }
}