package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EmptyArrayException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
    ValidationCheck validationCheck = new ValidationCheck();
    private final Map<Integer, Film> films = new HashMap<>();
    private int generatorId = 0;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен GET запрос");
        if (films.isEmpty()) {
            throw new EmptyArrayException("Список фильмов пуст.");
        }
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Получен POST запрос");
        validationCheck.validationCheckFilm(film);
        generatorId++;
        film.setId(generatorId);
        films.put(film.getId(), film);
        log.info("Список фильмов после добавления фильма: " + getAllFilms());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен PUT запрос");
        if (film.getId() != films.get(film.getId()).getId()) {
            throw new ValidationException("Ид не совпадают.");
        }
        validationCheck.validationCheckFilm(film);
        log.info("Фильм до правок: " + films.get(film.getId()));
        films.put(film.getId(), film);
        log.info("Фильм после правок: " + films.get(film.getId()));
        return film;
    }
}
