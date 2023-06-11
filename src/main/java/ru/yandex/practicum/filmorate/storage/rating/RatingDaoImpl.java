package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class RatingDaoImpl implements RatingDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Rating create(Rating rating) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("rating")
                .usingGeneratedKeyColumns("rating_id");
        int id = simpleJdbcInsert.executeAndReturnKey(rating.toMap()).intValue();
        rating.setId(id);
        return rating;
    }

    @Override
    public Rating getRatingById(int id) {
        String sqlQuery = "SELECT rating_id, name " +
                "FROM rating WHERE rating_id = ?";
        Rating rating;
        try {
            rating = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
        } catch (EmptyResultDataAccessException e) {
            throw new RatingNotFoundException(id);
        }
        return rating;
    }

    @Override
    public Collection<Rating> getAll() {
        String sqlQuery = "SELECT rating_id, name " +
                "FROM rating";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
