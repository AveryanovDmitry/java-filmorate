package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaStorageDataBase implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaStorageDataBase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String findById(Integer id) {
        log.info("MpaDbStorage. findById.");
        String sqlQuery = String.format("SELECT MPA_NAME " +
                "FROM MPA WHERE MPA_ID = %d", id);
        List<String> names = jdbcTemplate.queryForList(sqlQuery, String.class);
        if (names.size() != 1) {
            throw new NotFoundException("Некорректный id MPA.");
        }
        return names.get(0);
    }

    @Override
    public List<Mpa> findAll() {
        log.info("MpaDbStorage. findAll.");
        String sqlQuery = "SELECT MPA_ID, MPA_NAME FROM MPA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        log.info("MpaDbStorage. mapRowToMpa.");
        return new Mpa(resultSet.getInt("MPA_ID")
                , resultSet.getString("MPA_NAME"));
    }
}