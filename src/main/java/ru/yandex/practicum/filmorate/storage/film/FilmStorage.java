package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getAll();

    Film get(Integer id);

    Film add(Film film);

    Film update(Film film);

    Film delete(Integer id);
}
