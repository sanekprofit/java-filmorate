package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getAll() {
        return new HashMap<>(users);
    }

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public User update(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User add(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User delete(Long id) {
        return users.remove(id);
    }
}
