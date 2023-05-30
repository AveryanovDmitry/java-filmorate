package ru.yandex.practicum.filmorate.dao;

public interface LikesStorage {
    boolean addLike(Integer idFilm, Integer idUser);

    boolean deleteLike(Integer idFilm, Integer idUser);
}
