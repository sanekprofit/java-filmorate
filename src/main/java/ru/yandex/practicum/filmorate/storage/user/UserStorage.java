package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> getAll();

    User get(Long id);

    User update(User user);

    User add(User user);

    User delete(Long id);
}
