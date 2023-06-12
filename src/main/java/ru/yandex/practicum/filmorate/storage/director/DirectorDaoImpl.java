package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        int id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director save(Director director) {
        String sqlQuery = "UPDATE director SET " +
                "name = ? " +
                "WHERE director_id = ?";
        int result = jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());
        if (result == 0) {
            throw new DirectorNotFoundException(director.getId());
        }
        return director;
    }

    @Override
    public Director getDirectorById(int id) {
        String sqlQuery = "SELECT director_id, name " +
                "FROM director " +
                "WHERE director_id = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(id);
        }
        return director;
    }

    @Override
    public Collection<Director> getAll() {
        String sqlQuery = "SELECT director_id, name " +
                "FROM director";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public boolean deleteDirectorById(int id) {
        String sqlQuery = "DELETE from director " +
                "WHERE director_id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
