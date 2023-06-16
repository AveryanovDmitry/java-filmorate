package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    int reviewId;

    @NotBlank
    String content;

    @NotNull
    Boolean isPositive;

    @NotNull
    Integer userId;

    @NotNull
    Integer filmId;

    @ReadOnlyProperty
    @Value("0")
    int useful;

    public Map<String, ?> toMap() {
        Map<String, Object> reviewParam = new HashMap<>();
        reviewParam.put("REVIEW_ID", getReviewId());
        reviewParam.put("CONTENT", getContent());
        reviewParam.put("IS_POSITIVE", getIsPositive());
        reviewParam.put("USER_ID", getUserId());
        reviewParam.put("FILM_ID", getFilmId());
        reviewParam.put("USEFUL", getUseful());

        return reviewParam;
    }
}
