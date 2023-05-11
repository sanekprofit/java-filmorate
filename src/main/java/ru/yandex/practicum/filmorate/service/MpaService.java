package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Mpa> getMpa() {
        return mpaDbStorage.getMpa();
    }

    public Mpa getMpaById(Integer id) {
        return mpaDbStorage.getMpaById(id);
    }
}
