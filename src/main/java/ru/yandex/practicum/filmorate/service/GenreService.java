package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    final GenreStorage genreDbStorage;

    @Autowired
    public GenreService(GenreStorage genreDbStorage){
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAll(){
        return genreDbStorage.findAll();
    }

    public List<Genre> getGenresId(Integer id){
        return genreDbStorage.getGenres(id);
    }

    public Genre getById(Integer id){
        return new Genre(id, genreDbStorage.findById(id));
    }
}