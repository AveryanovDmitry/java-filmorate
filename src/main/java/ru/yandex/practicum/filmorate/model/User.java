package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    private int id;

    private String name;

    @Email(message = "электронная почта не может быть пустой и должна содержать символ @")
    @NotEmpty
    private String email;

    @NotBlank(message = "логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "^\\S*", message = "логин не может быть пустым и содержать пробелы")
    private String login;

    @NotNull
    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User(String name, String login, String email, LocalDate birthday) {
        this.name = name;
        this.login = login;
        this.email = email;
        this.birthday = birthday;
    }
}
