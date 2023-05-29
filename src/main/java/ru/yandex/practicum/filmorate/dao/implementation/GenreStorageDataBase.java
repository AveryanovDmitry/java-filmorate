package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Component
public class GenreStorageDataBase implements GenreStorage {

    private static final GenreMapper GENRE_MAPPER = new GenreMapper();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreStorageDataBase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Integer genre, Integer film) {
        jdbcTemplate.update("INSERT INTO GENRE_FILM(genre_id, film_id) values (?,?)", genre, film);
    }

    @Override
    public String findById(Integer id) {
        String sqlQuery = String.format("SELECT name FROM GENRE WHERE GENRE_ID = %d", id);
        List<String> names = jdbcTemplate.queryForList(sqlQuery, String.class);
        if (names.size() != 1) {
            throw new NotFoundException("Некорректный id жанра.");
        }
        return names.get(0);
    }

    @Override
    public List<Genre> getGenres(Integer idFilm) {
        String sqlQuery = String.format("SELECT GF.genre_id, g.name FROM GENRE_FILM as GF " +
                "JOIN GENRE as G on gf.genre_id = g.genre_id " +
                "WHERE gf.FILM_ID = %d", idFilm);
        return jdbcTemplate.query(sqlQuery, GENRE_MAPPER);
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlQuery, GENRE_MAPPER);
    }

    @Override
    public boolean deleteGenre(Integer idFilm, Integer idGenre) {
        String sqlQuery = String.format("SELECT COUNT(*)\n" +
                "FROM FILM_TO_GENRE\n" +
                "WHERE FILM_ID = %d AND GENRE_ID = %d", idFilm, idGenre);
        if (jdbcTemplate.queryForObject(sqlQuery, Integer.class) == 1) {
            return jdbcTemplate.update("DELETE FROM FILM_TO_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?"
                    , idFilm, idGenre) > 0;
        }
        return false;
    }
}