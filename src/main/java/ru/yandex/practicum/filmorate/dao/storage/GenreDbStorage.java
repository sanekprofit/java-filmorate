package ru.yandex.practicum.filmorate.dao.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public interface GenreDbStorage {

    List<Genre> getGenres();

    Genre getGenreById(Integer id);

}