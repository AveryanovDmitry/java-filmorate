package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikesStorage;

@Component
@RequiredArgsConstructor
public class LikesStorageDataBase implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(Integer idFilm, Integer idUser) {
        if (!findLikeUserToFilm(idFilm, idUser)) {
            return jdbcTemplate.update(String.format("INSERT INTO LIKES VALUES (%d, %d)", idFilm, idUser)) == 1;
        }
        return false;
    }

    @Override
    public boolean deleteLike(Integer idFilm, Integer idUser) {
        if (findLikeUserToFilm(idFilm, idUser)) {
            return jdbcTemplate.update("DELETE FROM LIKES WHERE film_id = ? AND user_id = ?", idFilm, idUser) > 0;
        }
        return false;
    }

    private boolean findLikeUserToFilm(Integer idFilm, Integer idUser) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM LIKES\n" +
                "WHERE FILM_ID = %d AND USER_ID = %d", idFilm, idUser);
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1;
    }

    public Integer getAmountLikesFilm(Integer idFilm) {
        String sqlQuery = String.format("SELECT COUNT(*) FROM LIKES WHERE FILM_ID = %d", idFilm);
        Integer rate = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        if (rate == null) {
            rate = 0;
        }
        return rate;
    }
}
