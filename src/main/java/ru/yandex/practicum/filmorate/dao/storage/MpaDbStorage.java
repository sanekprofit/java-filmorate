package ru.yandex.practicum.filmorate.dao.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public interface MpaDbStorage {
    List<Mpa> getMpa();
    Mpa getMpaById(Integer id);
}
