package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaStorageDataBase implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    @Override
    public Mpa findById(Integer id) {
        List<Mpa> names = jdbcTemplate.query(String.format("SELECT * FROM MPA WHERE MPA_ID = %d", id), mpaMapper);
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
        return new Mpa(resultSet.getInt("MPA_ID"),
                resultSet.getString("MPA_NAME"));
    }
}