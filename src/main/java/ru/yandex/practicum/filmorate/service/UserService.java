package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IdShouldntEqualsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private int generatorId = 0;
    private static final String GET_REQUEST_RECEIVED = "Получен GET запрос";
    private static final String POST_REQUEST_RECEIVED = "Получен POST запрос";
    private static final String PUT_REQUEST_RECEIVED = "Получен PUT запрос";
    private static final String DELETE_REQUEST_RECEIVED = "Получен DELETE запрос";

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        log.info(GET_REQUEST_RECEIVED);
        return new ArrayList<>(userStorage.getAll().values());
    }

    public User getUser(Long id) {
        log.info(GET_REQUEST_RECEIVED);
        if (!userStorage.getAll().containsKey(id)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        log.info("Пользователь с айди " + id + ": " + userStorage.get(id));
        return userStorage.get(id);
    }

    public List<User> getUserFriends(Long id) {
        log.info(GET_REQUEST_RECEIVED);
        if (!userStorage.getAll().containsKey(id)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        List<User> userFriends = new ArrayList<>();
        for (long otherId : userStorage.get(id).getFriends()) {
            userFriends.add(userStorage.get(otherId));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info(GET_REQUEST_RECEIVED);
        if (!userStorage.getAll().containsKey(id) || !userStorage.getAll().containsKey(otherId)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        List<User> commonFriends = new ArrayList<>();
        Set<Long> friends1 = userStorage.get(id).getFriends();
        Set<Long> friends2 = userStorage.get(otherId).getFriends();
        for (Long friendId1 : friends1) {
            for (Long friendId2 : friends2) {
                if (friendId1.equals(friendId2)) {
                    commonFriends.add(userStorage.get(friendId1));
                }
            }
        }
        commonFriends.remove(userStorage.get(id));
        commonFriends.remove(userStorage.get(otherId));
        log.info("Список общих друзей " + id + " и " + otherId + ": " + commonFriends);
        return commonFriends;
    }

    public User createUser(User user) {
        log.info(POST_REQUEST_RECEIVED);
        validationCheck(user);
        generatorId++;
        user.setId(generatorId);
        user.setFriends(new HashSet<>());
        userStorage.add(user);
        log.info("Новый пользователь в списке: " + userStorage.getAll().values());
        return user;
    }

    public User addFriend(Long id, Long friendId) {
        log.info(PUT_REQUEST_RECEIVED);
        if (!userStorage.getAll().containsKey(id) || !userStorage.getAll().containsKey(friendId)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        if (Objects.equals(id, friendId)) {
            log.error("Ошибка 400, неккоректно введены параметры");
            throw new IdShouldntEqualsException("Ид не должны быть равны.");
        }
        userStorage.get(id).getFriends().add(friendId);
        userStorage.get(friendId).getFriends().add(id);
        log.info("Список друзей " + id + " юзера после добавления друга: " + userStorage.get(id).getFriends());
        return userStorage.get(id);
    }

    public User deleteFriend(Long id, Long friendId) {
        log.info(DELETE_REQUEST_RECEIVED);
        if (!userStorage.getAll().containsKey(id) || !userStorage.getAll().containsKey(friendId)) {
            log.error("Ошибка 404, пользователь не был найден в списке");
            throw new UserNotFoundException("Пользователь не найден.");
        }
        if (Objects.equals(id, friendId)) {
            log.error("Ошибка 400, неккоректно введены параметры");
            throw new IdShouldntEqualsException("Ид не должны быть равны.");
        }
        userStorage.get(id).getFriends().remove(friendId);
        userStorage.get(friendId).getFriends().remove(id);
        log.info("Список друзей " + id + " юзера после удаления друга: " + userStorage.get(friendId).getFriends());
        return userStorage.get(id);
    }

    public User updateUser(User user) {
        log.info(PUT_REQUEST_RECEIVED);
        validationCheck(user);
        validationCheckPUTMethod(user);
        log.info("Пользователь до изменений: " + userStorage.get(user.getId()));
        userStorage.update(user);
        if (user.toString().contains("friends=null")) {
            user.setFriends(new HashSet<>());
        }
        log.info("После изменений: " + userStorage.get(user.getId()));
        return user;
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

    private void validationCheckPUTMethod(User user) {
        if (!userStorage.getAll().containsKey(user.getId())) {
            log.error("Ошибка 404, фильм не был найден в списке.");
            throw new FilmNotFoundException("Искомый пользователь не был найден.");
        }
        if (user.getId() != userStorage.get(user.getId()).getId()) {
            throw new ValidationException("Ид не совпадают.");
        }
    }
}
