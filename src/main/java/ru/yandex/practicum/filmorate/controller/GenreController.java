package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;
    private static final String GET_REQUEST_RECEIVED = "Получен GET запрос";

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        log.info(GET_REQUEST_RECEIVED);
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") Integer id) {
        log.info(GET_REQUEST_RECEIVED);
        return genreService.getGenreById(id);
    }
}
