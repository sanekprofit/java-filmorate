package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
    private final FilmService filmService;
    private static final String GET_REQUEST_RECEIVED = "Получен GET запрос";
    private static final String POST_REQUEST_RECEIVED = "Получен POST запрос";
    private static final String PUT_REQUEST_RECEIVED = "Получен PUT запрос";
    private static final String DELETE_REQUEST_RECEIVED = "Получен DELETE запрос";

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info(GET_REQUEST_RECEIVED);
        return filmService.getAllFilms();
    }

    @GetMapping(value = "/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        log.info(GET_REQUEST_RECEIVED);
        return filmService.getFilm(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Integer id,
                        @PathVariable("userId") Long userId) {
        log.info(PUT_REQUEST_RECEIVED);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Integer id,
                           @PathVariable("userId") Long userId) {
        log.info(DELETE_REQUEST_RECEIVED);
        return filmService.removeLike(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info(GET_REQUEST_RECEIVED);
        return filmService.getMostLikedFilms(count);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info(POST_REQUEST_RECEIVED);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info(PUT_REQUEST_RECEIVED);
        return filmService.updateFilm(film);
    }
}