package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;
    private static final String GET_REQUEST_RECEIVED = "Получен GET запрос";
    private static final String POST_REQUEST_RECEIVED = "Получен POST запрос";
    private static final String PUT_REQUEST_RECEIVED = "Получен PUT запрос";
    private static final String DELETE_REQUEST_RECEIVED = "Получен DELETE запрос";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info(GET_REQUEST_RECEIVED);
        return userService.getUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable("id") Long id) {
        log.info(GET_REQUEST_RECEIVED);
        return userService.getUser(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        log.info(PUT_REQUEST_RECEIVED);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        log.info(DELETE_REQUEST_RECEIVED);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long id,
                                       @PathVariable("otherId") Long otherId) {
        log.info(GET_REQUEST_RECEIVED);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Long id) {
        log.info(GET_REQUEST_RECEIVED);
        return userService.getUserFriends(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info(POST_REQUEST_RECEIVED);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info(PUT_REQUEST_RECEIVED);
        return userService.updateUser(user);
    }
}