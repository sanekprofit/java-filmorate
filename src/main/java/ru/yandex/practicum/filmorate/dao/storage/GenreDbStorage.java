package ru.yandex.practicum.filmorate.dao.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

@Repository
public interface GenreDbStorage {
    Genre getGenre();
    Genre getGenreById(Integer id);
}
