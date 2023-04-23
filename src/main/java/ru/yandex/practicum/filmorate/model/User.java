package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private int id;

    private String name;

    @Email(message = "электронная почта не может быть пустой и должна содержать символ @")
    private String email;

    @NotNull(message = "логин не может быть null")
    @NotBlank(message = "логин не может быть пустым и содержать пробелы")
    @Pattern(regexp = "^\\S*", message = "логин не может быть пустым и содержать пробелы")
    private String login;

    @NotNull
    @Past(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;
}
