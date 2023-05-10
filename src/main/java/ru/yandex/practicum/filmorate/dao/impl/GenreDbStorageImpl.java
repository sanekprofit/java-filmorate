package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
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
        List<Integer> genreIds = jdbcTemplate.queryForList("SELECT genre_id FROM genre", Integer.class);
        if (genreIds.contains(id)) {
            String sql = "SELECT * FROM genre WHERE genre_id = ?";
            return jdbcTemplate.query(sql, (rs) -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setTitle(rs.getString("title"));
                return genre;
            }, id);
        } else {
            log.error("Ошибка 404, не найден жанр с ID " + id);
            throw new IllegalArgumentException();
        }
    }
}
