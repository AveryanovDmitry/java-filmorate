package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreStorageDataBase implements GenreStorage {

    private final GenreMapper genreMapper;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void add(Set<Genre> genreSet, Integer film) {
        List<Genre> genres = new ArrayList<>(genreSet);
        jdbcTemplate.batchUpdate("INSERT INTO GENRE_FILM(genre_id, film_id) values (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, genres.get(i).getId());
                        ps.setInt(2, film);
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    @Override
    public Genre findById(Integer id) {
        String sqlQuery = String.format("SELECT * FROM GENRE WHERE GENRE_ID = %d", id);
        List<Genre> genreList = jdbcTemplate.query(sqlQuery, genreMapper);
        if (genreList.size() != 1) {
            throw new NotFoundException("Некорректный id жанра.");
        }
        return genreList.get(0);
    }

    @Override
    public Set<Genre> getFilmGenresByFilmId(Integer idFilm) {
        String sqlQuery = String.format("SELECT GF.genre_id, g.name FROM GENRE_FILM as GF " +
                "JOIN GENRE as G on gf.genre_id = g.genre_id " +
                "WHERE gf.FILM_ID = %d", idFilm);
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, genreMapper));
    }


    @Override
    public Set<Genre> findAll() {
        String sqlQuery = "SELECT * FROM GENRE";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, genreMapper));
    }

    @Override
    public boolean deleteGenre(Integer idFilm, Integer idGenre) {
        return jdbcTemplate.update("DELETE FROM FILM_TO_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?",
                idFilm, idGenre) > 0;
    }

    @Override
    public void updateGenre(Set<Genre> genres, Integer idFilm) {
        jdbcTemplate.update("DELETE FROM GENRE_FILM WHERE FILM_ID = ?", idFilm);
        add(genres, idFilm);
        log.info("Фанры фильм с id {} обновлёны", idFilm);
    }

    @Override
    public Map<Integer, LinkedHashSet<Genre>> getGenresListFilmsId(List<Integer> idFilms) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", idFilms);
        List<FoFindAllGenresForMap> genresAndFilms = namedParameterJdbcTemplate.query(
                "SELECT GF.film_id as film_id, GF.genre_id as genre_id, g.name as name FROM GENRE_FILM as GF " +
                        "JOIN GENRE as G on gf.genre_id = g.genre_id " +
                        "WHERE gf.FILM_ID in (:ids)",
                parameters,
                (rs, rowNum) -> new FoFindAllGenresForMap(rs.getInt("film_id"),
                        rs.getInt("genre_id"), rs.getString("name")));

        Map<Integer, LinkedHashSet<Genre>> genreMap = new LinkedHashMap<>();
        for (FoFindAllGenresForMap entity : genresAndFilms) {
            final LinkedHashSet<Genre> genres = genreMap.computeIfAbsent(entity.getFilmId(), k -> new LinkedHashSet<>());
            genres.add(new Genre(entity.getGenreId(), entity.nameGenre));
        }
        return genreMap;
    }

    @Data
    private class FoFindAllGenresForMap {
        private final Integer filmId;
        private final Integer genreId;
        private final String nameGenre;
    }
}