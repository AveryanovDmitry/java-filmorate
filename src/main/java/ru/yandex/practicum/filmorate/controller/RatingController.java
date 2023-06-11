package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class RatingController {

    RatingService genreService;

    @Autowired
    public RatingController(RatingService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public Rating create(@Valid @RequestBody Rating genre) {
        return genreService.create(genre);
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable int id) {
        return genreService.getRatingById(id);
    }

    @GetMapping
    public Collection<Rating> getAll() {
        return genreService.getAll();
    }
}
