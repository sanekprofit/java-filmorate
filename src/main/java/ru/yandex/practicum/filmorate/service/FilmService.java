package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final String GET_REQUEST_RECEIVED = "Получен GET запрос";
    private static final String POST_REQUEST_RECEIVED = "Получен POST запрос";
    private static final String PUT_REQUEST_RECEIVED = "Получен PUT запрос";
    private static final String DELETE_REQUEST_RECEIVED = "Получен DELETE запрос";

    private int generatorId = 0;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        log.info(GET_REQUEST_RECEIVED);
        log.info("Список фильмов: " + filmStorage.getAll().values());
        return new ArrayList<>(filmStorage.getAll().values());
    }

    public Film getFilm(Integer filmId) {
        log.info(GET_REQUEST_RECEIVED);
        if (!filmStorage.getAll().containsKey(filmId)) {
            log.error("Ошибка 404, фильм не был найден в списке.");
            throw new FilmNotFoundException("Искомый фильм не был найден.");
        }
        log.info("Фильм с ид " + filmId + ": " + filmStorage.get(filmId));
        return filmStorage.get(filmId);
    }

    public Film createFilm(Film film) {
        log.info(POST_REQUEST_RECEIVED);
        validationCheck(film);
        generatorId++;
        film.setId(generatorId);
        film.setLikes(new HashSet<>());
        filmStorage.add(film);
        log.info("Список фильмов после добавления фильма: " + filmStorage.getAll().values());
        return film;
    }

    public Film addLike(Integer filmId, Long userId) {
        log.info(PUT_REQUEST_RECEIVED);
        if (!filmStorage.getAll().containsKey(filmId)) {
            log.error("Ошибка 404, фильм не был найден в списке.");
            throw new FilmNotFoundException("Искомый фильм не был найден.");
        }
        if (!userStorage.getAll().containsKey(userId)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        filmStorage.get(filmId).getLikes().add(userId);
        log.info("Фильм после добавления лайка: " + filmStorage.get(filmId));
        return filmStorage.get(filmId);
    }

    public List<Film> getMostLikedFilms(Integer count) {
        log.info(GET_REQUEST_RECEIVED);
        List<Film> result;
        if (count == null || count <= 0) {
            throw new IllegalArgumentException();
        }
        result = filmStorage.getAll().values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());

        log.info("Топ " + count + " фильмов по лайкам: " + result);
        return result;
    }

    public Film removeLike(Integer filmId, Long userId) {
        log.info(DELETE_REQUEST_RECEIVED);
        if (!filmStorage.getAll().containsKey(filmId)) {
            log.error("Ошибка 404, фильм не был найден в списке.");
            throw new FilmNotFoundException("Искомый фильм не был найден.");
        }
        if (!userStorage.getAll().containsKey(userId)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        filmStorage.get(filmId).getLikes().remove(userId);
        log.info("Фильм после удаления лайка: " + filmStorage.get(filmId));
        return filmStorage.get(filmId);
    }

    public Film updateFilm(Film film) {
        log.info(PUT_REQUEST_RECEIVED);
        validationCheck(film);
        validationCheckPUTMethod(film);
        log.info("Фильм до правок: " + filmStorage.get(film.getId()));
        filmStorage.update(film);
        if (film.toString().contains("likes=null")) {
            film.setLikes(new HashSet<>());
        }
        log.info("Фильм после правок: " + filmStorage.get(film.getId()));
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
        if (!filmStorage.getAll().containsKey(film.getId())) {
            log.error("Ошибка 404, фильм не был найден в списке.");
            throw new FilmNotFoundException("Искомый фильм не был найден.");
        }
        if (film.getId() != filmStorage.get(film.getId()).getId()) {
            throw new ValidationException("Ид не совпадают.");
        }
    }
}