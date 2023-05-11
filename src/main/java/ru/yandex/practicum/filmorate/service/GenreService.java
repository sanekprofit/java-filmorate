package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreDbStorage.getGenreById(id);
    }
}