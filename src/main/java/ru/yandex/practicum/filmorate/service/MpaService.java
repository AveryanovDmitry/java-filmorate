package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {
    final MpaStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaStorage mpaDbStorage){
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> getAll(){
        return mpaDbStorage.findAll();
    }

    public Mpa getById(Integer id){
        return new Mpa(id, mpaDbStorage.findById(id));
    }
}