package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int generatorId = 0;

    @GetMapping("/users")
    public List<User> getUsers() {
        for (User user : users.values()) {
            return List.of(user);
        }
        return null;
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.toString().contains("name=null")) {
            user.setName(user.getLogin());
        }
        generatorId++;
        user.setId(generatorId);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        if (user.getId() != users.get(user.getId()).getId()) {
            throw new ValidationException("Ид не совпадают.");
        }
        users.put(user.getId(), user);
        return user;
    }
}
