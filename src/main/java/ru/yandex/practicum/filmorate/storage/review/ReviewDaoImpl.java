package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Review save(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID");
        int id = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();

        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {

        String sqlUpdate = "UPDATE REVIEWS\n" +
                "SET CONTENT =:content,\n" +
                "    IS_POSITIVE=:isPositive\n" +
                "WHERE REVIEW_ID = :reviewId";
        SqlParameterSource parameterUpdate = new BeanPropertySqlParameterSource(review);
        int result = namedParameterJdbcTemplate.update(sqlUpdate, parameterUpdate);

        if (result == 0) throw new ReviewNotFoundException(review.getReviewId());

        return load(review.getReviewId());
    }

    @Override
    public Review load(int id) {
        String sql = "SELECT * FROM REVIEWS WHERE REVIEW_ID = :reviewId";
        SqlParameterSource parameterUpdate = new MapSqlParameterSource().addValue("reviewId", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameterUpdate, this::mapRowToReview);
        } catch (DataAccessException e) {
            throw new ReviewNotFoundException(id);
        }
    }

    @Override
    public List<Review> loadAll(Integer filmId, int count) {
        StringBuilder sqlBuild = new StringBuilder("SELECT *\n" +
                "FROM REVIEWS");
        if (filmId != null) sqlBuild.append(" WHERE FILM_ID = :filmId");
        sqlBuild.append(" ORDER BY USEFUL DESC, REVIEW_ID LIMIT :count");

        SqlParameterSource parameterSelect = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("count", count);

        return namedParameterJdbcTemplate.query(sqlBuild.toString(), parameterSelect, this::mapRowToReview);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID =:reviewId";
        SqlParameterSource parameterDelete = new MapSqlParameterSource().addValue("reviewId", id);

        int result = namedParameterJdbcTemplate.update(sql, parameterDelete);
        if (result == 0) throw new ReviewNotFoundException(id);
    }

    @Override
    public void saveLike(int value, int id) {
        String sql = "UPDATE REVIEWS SET USEFUL = USEFUL + :value WHERE REVIEW_ID = :reviewId";
        SqlParameterSource parameterDelete = new MapSqlParameterSource()
                .addValue("value", value)
                .addValue("reviewId", id);

        int result = namedParameterJdbcTemplate.update(sql, parameterDelete);
        if (result == 0) throw new ReviewNotFoundException(id);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getInt("REVIEW_ID"))
                .content(resultSet.getString("CONTENT"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getInt("USER_ID"))
                .filmId(resultSet.getInt("FILM_ID"))
                .useful(resultSet.getInt("USEFUL"))
                .build();
    }
}
