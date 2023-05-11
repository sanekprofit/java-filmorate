package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.IdShouldntEqualsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserDbStorageImpl implements UserDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private long nextUserId = 0;

    public UserDbStorageImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM FILMORATE_USER", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            log.info("Найденые пользователи: {}", user);
            return user;
        });
    }


    @Override
    public User getUser(Long userId) {
        List<Long> userIds = jdbcTemplate.queryForList("SELECT user_id FROM FILMORATE_USER", Long.class);
        if (!userIds.contains(userId)) {
            log.error("Ошибка 404, пользователь не был найден в базе данных");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        String sql = "SELECT * FROM filmorate_user WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            log.info("Найденый пользователь: {}", user);
            return user;
        }, userId);
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM filmorate_user AS u " +
                "JOIN user_friends AS uf ON u.user_id = uf.friend_id " +
                "WHERE uf.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            log.info("Найденые друзья: {}", user);
            return user;
        }, userId);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM user_friends uf1 " +
                "JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id " +
                "JOIN filmorate_user u ON u.user_id = uf1.friend_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ?";

        List<User> commonFriends = jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setLogin(rs.getString("login"));
                    user.setName(rs.getString("name"));
                    user.setBirthday(rs.getDate("birthday").toLocalDate());
                    return user;
                }, id, otherId);

        if (commonFriends.isEmpty()) {
            log.info("Нет общих друзей у пользователей " + id + " и " + otherId);
        } else {
            log.info("Список общих друзей " + id + " и " + otherId + ": " + commonFriends);
        }

        return commonFriends;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO filmorate_user (user_id, login, name, email, birthday) " +
                "VALUES (?, ?, ?, ?, ?)";
        Long id = getNextUserId(); // метод, возвращающий следующий доступный id
        jdbcTemplate.update(sql,
                id,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User addFriend(Long id, Long friendId) {
        String checkFriendshipSql = "SELECT * FROM user_friends WHERE user_id = ? AND friend_id = ?";
        String addFriendSql = "INSERT INTO user_friends (user_id, friend_id, status) VALUES (?, ?, ?)";
        String updateFriendshipSql = "UPDATE user_friends SET status = ? WHERE user_id = ? AND friend_id = ?";

        User user = getUser(id);

        User friend = getUser(friendId);


        if (user == null || friend == null) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }

        if (Objects.equals(id, friendId)) {
            log.error("Ошибка 400, некорректно введены параметры");
            throw new IdShouldntEqualsException("Ид не должны быть равны.");
        }

        List<Map<String, Object>> friendship = jdbcTemplate.queryForList(checkFriendshipSql, id, friendId);

        if (friendship.isEmpty()) {
            jdbcTemplate.update(addFriendSql, id, friendId, "unconfirmed");
            log.info("Список друзей " + id + " юзера после добавления друга: " + getUserFriends(id));
        } else {
            String status = (String) friendship.get(0).get("status");
            if (status.equals("unconfirmed")) {
                jdbcTemplate.update(updateFriendshipSql, "confirmed", id, friendId);
                jdbcTemplate.update(updateFriendshipSql, "confirmed", friendId, id);
                log.info("Список друзей " + id + " юзера после подтверждения дружбы: " + getUserFriends(id));
            } else {
                log.info("Список друзей " + id + " юзера не изменился: " + getUserFriends(id));
            }
        }
        return user;
    }


    @Override
    public User deleteFriend(Long id, Long friendId) {
        String deleteSql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        String updateFriendshipSql = "UPDATE user_friends SET status = ? WHERE user_id = ? AND friend_id = ?";

        User user = getUser(id);

        User friend = getUser(friendId);

        if (user == null || friend == null) {
            log.error("Ошибка 404, пользователь не был найден в базе данных");
            throw new UserNotFoundException("Пользователь не найден.");
        }

        if (Objects.equals(id, friendId)) {
            log.error("Ошибка 400, неккоректно введены параметры");
            throw new IdShouldntEqualsException("Ид не должны быть равны.");
        }
        jdbcTemplate.update(deleteSql, id, friendId);
        jdbcTemplate.update(updateFriendshipSql, "unconfirmed", friendId, id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        List<Long> userIds = jdbcTemplate.queryForList("SELECT user_id FROM FILMORATE_USER", Long.class);
        if (userIds.contains(user.getId())) {
            String sql = "UPDATE filmorate_user " +
                    "SET login = ?, name = ?, email = ?, birthday = ? " +
                    "WHERE user_id = ?";
            jdbcTemplate.update(sql,
                    user.getLogin(),
                    user.getName(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
        } else {
            log.error("Ошибка 404, пользователь не был найден в базе данных");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return user;
    }

    private Long getNextUserId() {
        nextUserId++;
        return nextUserId;
    }
}