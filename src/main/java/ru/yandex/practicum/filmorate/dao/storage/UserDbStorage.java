package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDbStorage {
    List<User> getUsers();
    User getUser(Long id);
    List<User> getUserFriends(Long id);
    List<User> getCommonFriends(Long id, Long otherId);
    User createUser(User user);
    User addFriend(Long id, Long otherId);
    User deleteFriend(Long id, Long otherId);
    User updateUser(User user);
}
