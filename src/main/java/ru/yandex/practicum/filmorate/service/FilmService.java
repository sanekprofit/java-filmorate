package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service("filmService")
public class FilmService {
    private final FilmDbStorage filmDbStorage;

    public FilmService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    public Film getFilm(Integer filmId) {
        return filmDbStorage.getFilm(filmId);
    }

    public Film createFilm(Film film) {
        validationCheck(film);
        return filmDbStorage.createFilm(film);
    }

    public Film addLike(Integer filmId, Long userId) {
        return filmDbStorage.addLike(filmId, userId);
    }

    public List<Film> getMostLikedFilms(Integer limit) {
        return filmDbStorage.getMostLikedFilms(limit);
    }

    public Film removeLike(Integer filmId, Long userId) {
        return filmDbStorage.removeLike(filmId, userId);
    }

    public Film updateFilm(Film film) {
        validationCheck(film);
        return filmDbStorage.updateFilm(film);
    }

    private void validationCheck(Film film) {
        if (film.getName().isBlank()) {
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
}