package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
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

    private Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer idFriend) {
        friends.add(idFriend);
    }

    public void deleteFriend(Integer idFriend) {
        friends.remove(idFriend);
    }
}
