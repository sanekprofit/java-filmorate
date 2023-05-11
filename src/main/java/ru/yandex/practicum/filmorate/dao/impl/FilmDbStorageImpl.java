package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;

import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Service
public class FilmDbStorageImpl implements FilmDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private int nextFilmId = 0;

    public FilmDbStorageImpl(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.likes, f.release_date, " +
                "m.mpa_id, m.title AS mpa, " +
                "g.genre_id, g.title AS genres " +
                "FROM films AS f " +
                "LEFT JOIN mpa_list AS ml ON f.film_id = ml.film_id " +
                "LEFT JOIN mpa AS m ON ml.mpa_id = m.mpa_id " +
                "LEFT JOIN genre_list AS gl ON f.film_id = gl.film_id " +
                "LEFT JOIN genre AS g ON gl.genre_id = g.genre_id ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setLikes(getLikesSet(rs.getLong("likes")));
            film.setMpa(getMpa(rs.getInt("mpa_id"), rs.getString("mpa")));
            film.setGenres(getGenres(rs.getInt("genre_id"), rs.getString("genres")));
            return film;
        });
    }

    @Override
    public Film getFilm(Integer filmId) {
        List<Integer> filmIds = jdbcTemplate.queryForList("SELECT film_id FROM films", Integer.class);
        if (!filmIds.contains(filmId)) {
            log.error("Ошибка 404, фильм не был найден в базе данных!");
            throw new FilmNotFoundException("Фильм не найден.");
        }
        String filmSql = "SELECT f.film_id, f.name, f.description, f.duration, f.likes, f.release_date, " +
                "m.mpa_id, m.title AS mpa " +
                "FROM films AS f " +
                "LEFT JOIN mpa_list AS ml ON f.film_id = ml.film_id " +
                "LEFT JOIN mpa AS m ON ml.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(filmSql, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getInt("film_id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            f.setDuration(rs.getInt("duration"));
            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
            f.setLikes(getLikesSet(rs.getLong("likes")));
            f.setMpa(getMpa(rs.getInt("mpa_id"), rs.getString("mpa")));
            return f;
        }, filmId);

        String genresSql = "SELECT g.genre_id, g.title AS genre " +
                "FROM genre_list AS gl " +
                "LEFT JOIN genre AS g ON gl.genre_id = g.genre_id " +
                "WHERE gl.film_id = ? " +
                "ORDER BY gl.genre_id";
        List<Genre> genres = jdbcTemplate.query(genresSql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre"));
            return genre;
        }, filmId);

        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (film_id, name, description, duration, release_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        Integer id = getNextFilmId();
        jdbcTemplate.update(sql,
                id,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());

        Mpa mpaRating = film.getMpa();
        if (mpaRating != null) {
            mpaRating.setName(jdbcTemplate.queryForObject("SELECT title FROM mpa WHERE mpa_id = ?",
                    (rs, rowNum) -> rs.getString("title"),
                    mpaRating.getId()));
            jdbcTemplate.update("INSERT INTO mpa_list (film_id, mpa_id) VALUES (?, ?)", id, mpaRating.getId());
        }

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genre.setName(jdbcTemplate.queryForObject("SELECT title FROM genre WHERE genre_id = ?",
                        (rs, rowNum) -> rs.getString("title"),
                        genre.getId()));
                jdbcTemplate.update("INSERT INTO genre_list (film_id, genre_id) VALUES (?, ?)", id, genre.getId());
            }
        }

        film.setMpa(mpaRating);
        film.setGenres(genres);
        film.setId(id);
        return film;
    }

    @Override
    public Film addLike(Integer filmId, Long userId) {
        User user = userDbStorage.getUser(userId);
        if (user == null) {
            log.error("Ошибка 404, пользователь не был найден в базе данных");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        Film film = getFilm(filmId);
        if (film == null) {
            log.error("Ошибка 404, фильм не был найден в базе данных");
            throw new FilmNotFoundException("Пользователь не найден.");
        }
        String sql = "UPDATE films SET likes = likes + 1 WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);

        Set<Long> updatedLikes = new HashSet<>(film.getLikes());
        updatedLikes.add(userId);
        film.setLikes(updatedLikes);

        return film;
    }

    @Override
    public List<Film> getMostLikedFilms(Integer limit) {
        if (limit == null || limit < 0) {
            log.error("Ошибка 400, неккоректно введены параметры");
            throw new IllegalArgumentException();
        }

        List<Film> allFilms = getAllFilms();
        allFilms.sort(Comparator.comparingInt(f -> f.getLikes().size()));
        Collections.reverse(allFilms);

        if (allFilms.size() <= limit) {
            return allFilms;
        } else {
            return allFilms.subList(0, limit);
        }
    }

    @Override
    public Film removeLike(Integer filmId, Long userId) {
        User user = userDbStorage.getUser(userId);
        if (user == null) {
            log.error("Ошибка 404, пользователь не был найден в базе данных");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        Film film = getFilm(filmId);
        if (film == null) {
            log.error("Ошибка 404, фильм не был найден в базе данных");
            throw new FilmNotFoundException("Пользователь не найден.");
        }
        String sql = "UPDATE films SET likes = likes - 1 WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        Set<Long> updatedLikes = new HashSet<>(film.getLikes());
        updatedLikes.remove(userId);
        film.setLikes(updatedLikes);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        List<Integer> filmIds = jdbcTemplate.queryForList("SELECT film_id FROM films", Integer.class);
        if (!filmIds.contains(film.getId())) {
            log.error("Ошибка 404, фильм не был найден в базе данных!");
            throw new FilmNotFoundException("Фильм не найден.");
        }
        String sql = "UPDATE FILMS " +
                "SET NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getId());
        Mpa mpaRating = film.getMpa();
        Set<Genre> genres = film.getGenres();
        if (mpaRating != null) {
            mpaRating.setName(jdbcTemplate.queryForObject("SELECT title " +
                    "FROM mpa " +
                    "WHERE mpa_id = ?", (rs, rowNum) -> rs.getString("title"), mpaRating.getId()));
            jdbcTemplate.update("UPDATE mpa_list " +
                    "SET mpa_id = ? " +
                    "WHERE film_id = ?", mpaRating.getId(), film.getId());
        }
        if (genres != null) {
            // Сначала удаляем все существующие связи жанров с фильмом
            jdbcTemplate.update("DELETE FROM genre_list WHERE film_id = ?", film.getId());
            // Затем добавляем новые связи для каждого жанра с сортировкой по genre_list.genre_id
            for (Genre genre : genres) {
                genre.setName(jdbcTemplate.queryForObject("SELECT title FROM genre WHERE genre_id = ?",
                        (rs, rowNum) -> rs.getString("title"),
                        genre.getId()));
                jdbcTemplate.update("INSERT INTO genre_list (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
            // Получаем жанры с сортировкой по genre_list.genre_id
            genres = getGenres(film.getId());
        }
        film.setMpa(mpaRating);
        film.setGenres(genres);
        return film;
    }

    private Integer getNextFilmId() {
        nextFilmId++;
        return nextFilmId;
    }

    private Set<Long> getLikesSet(Long likes) {
        if (likes == null) {
            return new HashSet<>();
        } else {
            return Collections.singleton(likes);
        }
    }

    private Mpa getMpa(Integer id, String name) {
        if (id == null || name == null) {
            return null;
        } else {
            Mpa mpa = new Mpa();
            mpa.setId(id);
            mpa.setName(name);
            return mpa;
        }
    }

    private Set<Genre> getGenres(Integer id, String genreString) {
        Set<Genre> genres = new HashSet<>();
        if (genreString == null || id == null) {
            return genres;
        } else {
            String[] genreNames = genreString.split(", ");
            for (String name : genreNames) {
                Genre genre = new Genre();
                genre.setId(id);
                genre.setName(name);
                genres.add(genre);
            }
            return genres;
        }
    }

    private Set<Genre> getGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.title AS name " +
                "FROM genre_list AS gl " +
                "JOIN genre AS g ON gl.genre_id = g.genre_id " +
                "WHERE gl.film_id = ? " +
                "ORDER BY gl.genre_id";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }
}