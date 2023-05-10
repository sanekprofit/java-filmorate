package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

@Service
public class GenreDbStorageImpl implements GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre() {
        String sql = "SELECT * FROM genre";
        return jdbcTemplate.query(sql, (rs) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setTitle(rs.getString("title"));
            return genre;
        });
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.query(sql, (rs) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setTitle(rs.getString("title"));
            return genre;
        }, id);
    }
}
