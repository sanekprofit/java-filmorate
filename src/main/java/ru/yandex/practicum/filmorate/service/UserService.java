package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
public class UserService {
    private final UserDbStorage userDbStorage;

    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public List<User> getUsers() {
        return userDbStorage.getUsers();
    }

    public User getUser(Long id) {
        return userDbStorage.getUser(id);
    }

    public List<User> getUserFriends(Long id) {
        return userDbStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userDbStorage.getCommonFriends(id, otherId);
    }

    public User createUser(User user) {
        validationCheck(user);
        return userDbStorage.createUser(user);
    }

    public User addFriend(Long id, Long friendId) {
        return userDbStorage.addFriend(id, friendId);
    }

    public User deleteFriend(Long id, Long friendId) {
        return userDbStorage.deleteFriend(id, friendId);
    }

    public User updateUser(User user) {
        validationCheck(user);
        return userDbStorage.updateUser(user);
    }

    private void validationCheck(User user) {
        if (user.toString().contains("email=null") || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.toString().contains("login=null") || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.toString().contains("name=null") || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}