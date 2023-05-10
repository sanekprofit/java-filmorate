package ru.yandex.practicum.filmorate.dao.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

@Repository
public interface MpaDbStorage {
    Mpa getMpa();
    Mpa getMpaById(Integer id);
}
