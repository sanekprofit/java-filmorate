package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmDbStorage {
    List<Film> getAllFilms();
    Film getFilm(Integer filmId);
    Film createFilm(Film film);
    Film addLike(Integer filmId, Long userId);
    List<Film> getMostLikedFilms(Integer limit);
    Film removeLike(Integer filmId, Long userId);
    Film updateFilm(Film film);
}