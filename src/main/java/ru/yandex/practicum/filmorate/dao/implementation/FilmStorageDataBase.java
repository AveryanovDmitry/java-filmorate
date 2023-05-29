package ru.yandex.practicum.filmorate.dao.implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Slf4j
public class FilmStorageDataBase implements FilmStorage {
    private static final FilmMapper FILM_MAPPER = new FilmMapper();
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreDb;

    @Autowired
    public FilmStorageDataBase(JdbcTemplate jdbcTemplate, GenreStorage genreDb) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDb = genreDb;
    }

    @Override
    public Film add(Film film) {
        System.out.println(film);
        jdbcTemplate.update("INSERT INTO FILMS(name, mpa_id, description, releaseDate, duration, rate)" +
                        " values (?,?,?,?,?,?)",
                film.getName(), film.getMpa().getId(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate());
        log.info("Фильм, {} добавлен", film.getName());

        Film filmAdded = jdbcTemplate.query("SELECT *, mpa.MPA_NAME " +
                        "FROM FILMS as films " +
                        "JOIN MPA as mpa ON films.mpa_id = mpa.mpa_id " +
                        "WHERE films.name = ? and films.description = ? and films.releaseDate = ?",
                FILM_MAPPER, film.getName(), film.getDescription(), film.getReleaseDate()).get(0);

        if (!film.getGenres().isEmpty()) {
            film.getGenres().forEach((genre) -> genreDb.add(genre.getId(), filmAdded.getId()));
            log.info("Добавлены жанры: {}", genreDb.getGenres(filmAdded.getId()));
            filmAdded.setGenres(genreDb.getGenres(filmAdded.getId()));
        }
        return filmAdded;
    }

    public Film update(Film film) {
        String sqlQuery = "UPDATE FILMS " +
                "SET name = ?, mpa_id = ?, description = ? , releaseDate = ?, duration = ?, rate = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getMpa().getId(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getId());
        jdbcTemplate.update("DELETE FROM GENRE_FILM WHERE FILM_ID = ?", film.getId());
        LinkedHashSet<Genre> uniqGenres = new LinkedHashSet<>(film.getGenres());
        uniqGenres.forEach((genre) -> genreDb.add(genre.getId(), film.getId()));
        log.info("Фильм, {} обновлён", film.getName());
        return getById(film.getId()).get();
    }

    public List<Film> getFilms() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM FILMS JOIN MPA ON FILMS.mpa_id = MPA.mpa_id",
                new FilmMapper());
        if (!films.isEmpty()) {
            films.forEach((film) -> film.setGenres(genreDb.getGenres(film.getId())));
        }
        return films;
    }

    public Optional<Film> getById(Integer id) {
        List<Film> film = jdbcTemplate.query("SELECT * FROM FILMS " +
                "JOIN MPA ON FILMS.mpa_id = MPA.mpa_id " +
                "WHERE FILMS.id = ?", FILM_MAPPER, id);
        if (!film.isEmpty()) {
            Film findFilm = film.get(0);
            findFilm.setGenres(genreDb.getGenres(findFilm.getId()));
            return Optional.of(findFilm);
        }
        return Optional.empty();
    }

    @Override
    public boolean addLike(Integer idFilm, Integer idUser) {
        if (!findLikeUserToFilm(idFilm, idUser)) {
            return jdbcTemplate.update(String.format("INSERT INTO LIKES VALUES (%d, %d)", idFilm, idUser)) == 1;
        }
        return false;
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
        return jdbcTemplate.query(sqlQuery, FILM_MAPPER);
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
}
