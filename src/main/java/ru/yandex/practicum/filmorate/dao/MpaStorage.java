package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public interface MpaStorage {
    String findById(Integer id);

    List<Mpa> findAll();
}