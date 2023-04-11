package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Map<Integer, Film> getAll() {
        return new HashMap<>(films);
    }

    @Override
    public Film get(Integer id) {
        return films.get(id);
    }

    @Override
    public Film add(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Film update(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Film delete(Integer id) {
        return films.remove(id);
    }
}
