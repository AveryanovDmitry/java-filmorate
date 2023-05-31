package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmStorageDataBase implements FilmStorage {
    private final FilmMapper filmMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("FILMS")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> filmInMap = Map.of("name", film.getName(), "mpa_id", film.getMpa().getId(),
                "description", film.getDescription(), "releaseDate", film.getReleaseDate(),
                "duration", film.getDuration());
        int idUserInBD = simpleJdbcInsert.executeAndReturnKey(filmInMap).intValue();
        film.setId(idUserInBD);

        return film;
    }

    public void update(Film film) {
        String sqlQuery = "UPDATE FILMS " +
                "SET name = ?, mpa_id = ?, description = ? , releaseDate = ?, duration = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getMpa().getId(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
    }

    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS JOIN MPA ON FILMS.mpa_id = MPA.mpa_id", new FilmMapper());
    }

    public Optional<Film> getById(Integer id) {
        List<Film> film = jdbcTemplate.query("SELECT * FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "WHERE FILMS.id = ?", filmMapper, id);
        if (!film.isEmpty()) {
            Film findFilm = film.get(0);
            return Optional.of(findFilm);
        }
        return Optional.empty();
    }

    @Override
    public List<Film> mostPopulars(Integer limit) {
        String sqlQuery = String.format("SELECT films.*, " +
                "count(likes.film_id) as count_likes, mpa.mpa_name as mpa_name " +
                "FROM FILMS as films " +
                "JOIN MPA as mpa ON films.mpa_id = mpa.mpa_id " +
                "LEFT JOIN LIKES as likes on films.id = likes.film_id " +
                "GROUP BY films.id " +
                "ORDER BY count_likes DESC, films.name " +
                "LIMIT %d", limit);
        return jdbcTemplate.query(sqlQuery, filmMapper);
    }

    public void checkId(Integer id) {
        String sqlQuery = String.format("select exists(SELECT * FROM FILMS WHERE id = %d)", id);
        if (!Boolean.TRUE.equals(jdbcTemplate.queryForObject(sqlQuery, Boolean.class))) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
    }
}
