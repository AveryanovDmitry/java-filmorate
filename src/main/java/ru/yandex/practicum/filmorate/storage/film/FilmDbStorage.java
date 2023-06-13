package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();

        if (film.getGenres() != null) {
            addGenresToFilm(id, film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        }
        if (film.getDirectors() != null) {
            addDirectorsToFilm(id, film.getDirectors().stream().map(Director::getId).collect(Collectors.toList()));
        }
        film.setId(id);
        return film;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ? , duration = ?, rating_id = ?" +
                "where film_id = ?";
        int result = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (result == 0) {
            throw new FilmNotFoundException(film.getId());
        }

        if (film.getGenres() != null) {
            Collection<Genre> genres = getGenresByFilmId(film.getId());
            solveGenresAdd(film.getId(), genres, film.getGenres());
        }
        Collection<Director> directors = getDirectorsByFilmId(film.getId());
        if (film.getDirectors() != null) {
            solveDirectorsAdd(film.getId(), directors, film.getDirectors());
        } else {
            removeDirectorsFromFilm(film.getId(), directors.stream().map(Director::getId).collect(Collectors.toList()));
        }
        film.setGenres(getGenresByFilmId(film.getId()));
        film.setDirectors(getDirectorsByFilmId(film.getId()));
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = "SELECT f.film_id, " +
                "f.name film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name rating_name " +
                "FROM film f " +
                "JOIN rating r ON f.rating_id = r.rating_id";
        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    @Override
    public Collection<Film> getFilmsByIds(Collection<Integer> filmIds) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("films",
                        filmIds);
        String sqlQuery = "SELECT f.film_id, " +
                "f.name film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name rating_name " +
                "FROM film f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id in (:films)";
        Collection<Film> films = namedJdbcTemplate.query(sqlQuery, parameters, this::mapRowToFilm);
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT f.film_id, " +
                "f.name film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name rating_name " +
                "FROM film f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(id);
        }
        if (film != null) {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setDirectors(getDirectorsByFilmId(film.getId()));
        }
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        SqlRowSet likeRow = findLike(filmId, userId);
        if (likeRow.next()) {
            throw new AlreadyFriendsException();
        } else {
            String sqlQuery = "INSERT INTO likes(film_id, user_id) " +
                    "VALUES(?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, userId);
        }
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(int directorId, String sortBy) {
        String sqlQuery = "SELECT f.film_id, " +
                "f.name film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name rating_name, " +
                "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) likes " +
                "FROM film_director fd " +
                "JOIN film f ON fd.film_id = f.film_id " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "WHERE fd.director_id = ? ";
        if (sortBy.equals("year")) {
            sqlQuery += "" +
                    "ORDER BY f.release_date";
        } else if (sortBy.equals("likes")) {
            sqlQuery += "ORDER BY likes DESC";
        }

        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm,
                directorId);
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    private SqlRowSet findLike(int filmId, int userId) {
        String sqlQuery = "SELECT * " +
                "FROM likes " +
                "WHERE film_id = ? " +
                "AND user_id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE film_id = ? " +
                "AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.film_id, " +
                "f.name film_name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rating_id, " +
                "r.name rating_name, " +
                "l.likes likes " +
                "FROM film f " +
                "JOIN rating r ON f.rating_id = r.rating_id " +
                "LEFT JOIN (SELECT film_id, " +
                "COUNT(film_id) " +
                "likes " +
                "FROM likes " +
                "GROUP BY film_id) l ON f.film_id = l.film_id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";
        Collection<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        setGenresToFilms(films);
        setDirectorsToFilms(films);
        return films;
    }

    private Collection<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT genre_id, " +
                "name " +
                "FROM genre " +
                "WHERE genre_id IN (SELECT genre_id " +
                "FROM film_genre " +
                "WHERE film_id = ?)";
        return jdbcTemplate.query(sqlQuery, ((rs, rowNum) -> Genre.builder().id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build()), filmId);
    }

    private void setGenresToFilms(Collection<Film> films) {
        Map<Integer, Film> filmsMap = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("films",
                        filmsMap.keySet());
        String sqlQuery = "SELECT f.film_id film_id, g.genre_id genre_id, g.name name " +
                "FROM genre g " +
                "JOIN film_genre f ON f.genre_id=g.genre_id " +
                "WHERE f.film_id in (:films)";
        SqlRowSet rowSet = namedJdbcTemplate.queryForRowSet(sqlQuery, parameters);
        Map<Integer, Collection<Genre>> filmGenres = new HashMap<>();
        while (rowSet.next()) {
            Genre genre = Genre.builder().id(rowSet.getInt("genre_id"))
                    .name(rowSet.getString("name"))
                    .build();
            int filmId = rowSet.getInt("film_id");
            Collection<Genre> genres = filmGenres.getOrDefault(filmId, new HashSet<>());
            genres.add(genre);
            filmGenres.put(filmId, genres);
        }
        films.forEach(film -> film.setGenres(filmGenres.getOrDefault(film.getId(), new HashSet<>())));
    }

    private void solveGenresAdd(int filmId, Collection<Genre> oldGenres, Collection<Genre> newGenres) {
        Set<Integer> oldGenresIds = oldGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Integer> newGenresIds = newGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        List<Integer> genresToAdd = newGenresIds.stream().filter(id -> !oldGenresIds.contains(id)).collect(Collectors.toList());
        List<Integer> genresToDel = oldGenresIds.stream().filter(id -> !newGenresIds.contains(id)).collect(Collectors.toList());
        addGenresToFilm(filmId, genresToAdd);
        removeGenresFromFilm(filmId, genresToDel);
    }

    private void removeGenresFromFilm(int filmId, List<Integer> genresToDel) {
        if (genresToDel.size() > 0) {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("film_id", filmId)
                    .addValue("genre_ids", genresToDel);
            String sqlQuery = "DELETE FROM film_genre " +
                    "WHERE film_id = :film_id " +
                    "AND genre_id IN (:genre_ids)";
            namedJdbcTemplate.update(sqlQuery, parameters);
        }
    }

    private void addGenresToFilm(int filmId, List<Integer> genresToAdd) {
        if (genresToAdd.size() > 0) {
            String sqlQuery = "INSERT INTO film_genre(film_id, genre_id)" +
                    "VALUES(?, ?)";
            for (Integer genreId : genresToAdd) {
                jdbcTemplate.update(sqlQuery, filmId, genreId);
            }
        }
    }

    private void solveDirectorsAdd(int filmId, Collection<Director> oldDirectors, Collection<Director> newDirectors) {
        Set<Integer> oldDirectorsIds = oldDirectors.stream().map(Director::getId).collect(Collectors.toSet());
        Set<Integer> newDirectorsIds = newDirectors.stream().map(Director::getId).collect(Collectors.toSet());
        List<Integer> directorsToAdd = newDirectorsIds.stream().filter(id -> !oldDirectorsIds.contains(id)).collect(Collectors.toList());
        List<Integer> directorsToDel = oldDirectorsIds.stream().filter(id -> !newDirectorsIds.contains(id)).collect(Collectors.toList());
        addDirectorsToFilm(filmId, directorsToAdd);
        removeDirectorsFromFilm(filmId, directorsToDel);
    }

    private void removeDirectorsFromFilm(int filmId, List<Integer> directorsToDel) {
        if (directorsToDel.size() > 0) {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("film_id", filmId)
                    .addValue("director_ids", directorsToDel);
            String sqlQuery = "DELETE FROM film_director " +
                    "WHERE film_id = :film_id " +
                    "AND director_id IN (:director_ids)";
            namedJdbcTemplate.update(sqlQuery, parameters);
        }
    }

    private void addDirectorsToFilm(int filmId, List<Integer> directorsToAdd) {
        if (directorsToAdd.size() > 0) {
            String sqlQuery = "INSERT INTO film_director(film_id, director_id)" +
                    "VALUES(?, ?)";
            for (Integer directorId : directorsToAdd) {
                jdbcTemplate.update(sqlQuery, filmId, directorId);
            }
        }
    }

    private void setDirectorsToFilms(Collection<Film> films) {
        Map<Integer, Film> filmsMap = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("films",
                        filmsMap.keySet());
        String sqlQuery = "SELECT f.film_id film_id, d.director_id director_id, d.name name " +
                "FROM director d " +
                "JOIN film_director f ON f.director_id=d.director_id " +
                "WHERE f.film_id in (:films)";
        SqlRowSet rowSet = namedJdbcTemplate.queryForRowSet(sqlQuery, parameters);
        Map<Integer, Collection<Director>> filmDirectors = new HashMap<>();
        while (rowSet.next()) {
            Director director = Director.builder().id(rowSet.getInt("director_id"))
                    .name(rowSet.getString("name"))
                    .build();
            int filmId = rowSet.getInt("film_id");
            Collection<Director> directors = filmDirectors.getOrDefault(filmId, new HashSet<>());
            directors.add(director);
            filmDirectors.put(filmId, directors);
        }
        films.forEach(film -> film.setDirectors(filmDirectors.getOrDefault(film.getId(), new HashSet<>())));
    }

    private Collection<Director> getDirectorsByFilmId(int filmId) {
        String sqlQuery = "SELECT director_id, " +
                "name " +
                "FROM director " +
                "WHERE director_id IN (SELECT director_id " +
                "FROM film_director " +
                "WHERE film_id = ?)";
        return jdbcTemplate.query(sqlQuery, ((rs, rowNum) -> Director.builder().id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build()), filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getObject("release_date", LocalDate.class))
                .duration(resultSet.getInt("duration"))
                .mpa(new Rating(resultSet.getInt("rating_id"), resultSet.getString("rating_name")))
                .build();
    }
}
