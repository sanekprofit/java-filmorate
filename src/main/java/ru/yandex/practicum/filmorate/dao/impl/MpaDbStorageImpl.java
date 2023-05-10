package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Service
public class MpaDbStorageImpl implements MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, (rs) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setTitle(rs.getString("title"));
            return mpa;
        });
    }

    @Override
    public Mpa getMpaById(Integer id) {
        List<Integer> mpaIds = jdbcTemplate.queryForList("SELECT mpa_id FROM mpa", Integer.class);
        if (mpaIds.contains(id)) {
            String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
            return jdbcTemplate.query(sql, (rs) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setTitle(rs.getString("title"));
                return mpa;
            }, id);
        } else {
            log.error("Ошибка 404, не найден рейтинг MPA с ID " + id);
            throw new IllegalArgumentException();
        }
    }
}
