package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.EmptyArrayException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int generatorId = 0;

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен GET запрос");
        if (users.isEmpty()) {
            throw new EmptyArrayException("Список пользователей пуст.");
        }
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен POST запрос");
        validationCheck(user);
        generatorId++;
        user.setId(generatorId);
        users.put(user.getId(), user);
        log.info("Список пользователей после добавление нового юзера: " + getUsers());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получен PUT запрос");
        validationCheck(user);
        validationCheckPUTMethod(user);
        log.info("Пользователь до изменений: " + users.get(user.getId()));
        users.put(user.getId(), user);
        log.info("Изменённый пользователь: " + users.get(user.getId()));
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
        if (user.toString().contains("name=null")) {
            user.setName(user.getLogin());
        }
    }

    private void validationCheckPUTMethod(User user) {
        if (user.getId() != users.get(user.getId()).getId()) {
            throw new ValidationException("Ид не совпадают.");
        }
    }
}
