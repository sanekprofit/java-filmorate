package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EmptyArrayException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {
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
        validationCheck(film);
        generatorId++;
        film.setId(generatorId);
        films.put(film.getId(), film);
        log.info("Список фильмов после добавления фильма: " + getAllFilms());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен PUT запрос");
        validationCheck(film);
        validationCheckPUTMethod(film);
        log.info("Фильм до правок: " + films.get(film.getId()));
        films.put(film.getId(), film);
        log.info("Фильм после правок: " + films.get(film.getId()));
        return film;
    }

    private void validationCheck(Film film) {
        if (film.toString().contains("name=null") || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания не может быть больше 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    private void validationCheckPUTMethod(Film film) {
        if (film.getId() != films.get(film.getId()).getId()) {
            throw new ValidationException("Ид не совпадают.");
        }
    }
}
