package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dao.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    @Autowired
    UserController uc;
    @Autowired
    FilmController fc;
    @Autowired
    UserDbStorage userDbStorage;
    @Autowired
    FilmDbStorage filmDbStorage;
    int duration = 100;
    String login = "login";
    String name = "name";
    String email = "mail@mail.com";
    String description = "description";
    LocalDate releaseDate = LocalDate.of(2000, 5, 12);
    LocalDate birthDay = LocalDate.of(1970, 11, 28);

    @Test
    void emptyUserNameShouldSetLoginForName() {
        User user = new User();
        user.setId(1);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        uc.createUser(user);
        assertEquals("login", user.getName());
    }

    @Test
    void invalidUserEmailShouldReturnValidationException() {
        User user = new User();
        user.setId(1);
        user.setName(name);
        user.setLogin(login);
        user.setEmail("invalid mail");
        user.setBirthday(birthDay);
        assertThrows(ValidationException.class, () -> uc.createUser(user));
    }

    @Test
    void invalidUserBirthDayShouldReturnValidationException() {
        User user = new User();
        user.setId(1);
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(LocalDate.MAX);
        assertThrows(ValidationException.class, () -> uc.createUser(user));
    }

    @Test
    void emptyUserLoginShouldReturnValidationException() {
        User user = new User();
        user.setId(1);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthDay);
        assertThrows(ValidationException.class, () -> uc.createUser(user));
    }

    @Test
    void emptyFilmNameShouldReturnValidationException() {
        Film film = new Film();
        film.setId(1);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        assertThrows(ValidationException.class, () -> fc.createFilm(film));
    }

    @Test
    void tooLongFilmDescriptionShouldReturnValidationException() {
        String longString = "x".repeat(201);
        Film film = new Film();
        film.setId(1);
        film.setName(name);
        film.setDescription(longString);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        assertThrows(ValidationException.class, () -> fc.createFilm(film));
    }

    @Test
    void invalidFilmReleaseDateShouldReturnValidationException() {
        Film film = new Film();
        film.setId(1);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.MIN);
        film.setDuration(duration);
        assertThrows(ValidationException.class, () -> fc.createFilm(film));
    }

    @Test
    void negativeFilmDurationShouldReturnValidationException() {
        Film film = new Film();
        film.setId(1);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> fc.createFilm(film));
    }

    @Test
    void testCreateUserAndGetUser() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        User user1 = userDbStorage.getUser(user.getId());
        assertNotNull(user1);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void testGetUsers() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        List<User> users = userDbStorage.getUsers();
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void testGetUserFriendsAndAddFriend() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        User userFriend = new User();
        userFriend.setName(name);
        userFriend.setLogin(login);
        userFriend.setEmail(email);
        userFriend.setBirthday(birthDay);
        userDbStorage.createUser(userFriend);
        userDbStorage.addFriend(user.getId(), userFriend.getId());
        List<User> userFriends = userDbStorage.getUserFriends(user.getId());
        assertThat(userFriends.get(0)).hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    void testGetCommonFriendsAndAddFriend() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        User userFriend = new User();
        userFriend.setName(name);
        userFriend.setLogin(login);
        userFriend.setEmail(email);
        userFriend.setBirthday(birthDay);
        userDbStorage.createUser(userFriend);
        User commonFriend = new User();
        commonFriend.setName(name);
        commonFriend.setLogin(login);
        commonFriend.setEmail(email);
        commonFriend.setBirthday(birthDay);
        userDbStorage.createUser(commonFriend);
        userDbStorage.addFriend(user.getId(), commonFriend.getId());
        userDbStorage.addFriend(userFriend.getId(), commonFriend.getId());
        userDbStorage.addFriend(userFriend.getId(), user.getId());
        List<User> commonFriends = userDbStorage.getCommonFriends(user.getId(), userFriend.getId());
        assertThat(commonFriends.get(0)).hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    void testDeleteFriend() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        assertNotNull(userDbStorage.getUser(user.getId()));
        User userFriend = new User();
        userFriend.setName(name);
        userFriend.setLogin(login);
        userFriend.setEmail(email);
        userFriend.setBirthday(birthDay);
        userDbStorage.createUser(userFriend);
        assertNotNull(userDbStorage.getUser(userFriend.getId()));
        userDbStorage.addFriend(user.getId(), userFriend.getId());
        userDbStorage.addFriend(userFriend.getId(), user.getId());
        List<User> userWithFriend = userDbStorage.getUserFriends(user.getId());
        assertThat(userWithFriend.get(0)).hasFieldOrPropertyWithValue("id", 2L);
        userDbStorage.deleteFriend(user.getId(), userFriend.getId());
        List<User> userWithNoFriend = userDbStorage.getUserFriends(user.getId());
        assertEquals(userWithNoFriend.size(), 0);
    }

    @Test
    void testUserUpdate() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        User user1 = userDbStorage.getUser(user.getId());
        assertNotNull(user1);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);
        User newUser = new User();
        newUser.setId(1);
        newUser.setName(name);
        newUser.setLogin("updatedLogin");
        newUser.setEmail(email);
        newUser.setBirthday(birthDay);
        userDbStorage.updateUser(newUser);
        User updatedUser = userDbStorage.getUser(user.getId());
        assertNotNull(updatedUser);
        assertThat(updatedUser).hasFieldOrPropertyWithValue("login", "updatedLogin");
    }

    @Test
    void getAllFilms() {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        filmDbStorage.createFilm(film);
        List<Film> films = filmDbStorage.getAllFilms();
        assertEquals(films.size(), 1);
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void getFilm() {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        filmDbStorage.createFilm(film);
        Film film1 = filmDbStorage.getFilm(film.getId());
        assertNotNull(film1);
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void addLike() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        Film likeFilm = new Film();
        likeFilm.setName(name);
        likeFilm.setDescription(description);
        likeFilm.setReleaseDate(releaseDate);
        likeFilm.setDuration(duration);
        filmDbStorage.createFilm(likeFilm);
        filmDbStorage.addLike(likeFilm.getId(), user.getId());
        Film likedFilm = filmDbStorage.getFilm(likeFilm.getId());
        assertThat(likedFilm).hasFieldOrPropertyWithValue("likes", Set.of(1L));
    }

    @Test
    void removeLike() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        Film noLikeFilm = new Film();
        noLikeFilm.setName(name);
        noLikeFilm.setDescription(description);
        noLikeFilm.setReleaseDate(releaseDate);
        noLikeFilm.setDuration(duration);
        filmDbStorage.createFilm(noLikeFilm);
        filmDbStorage.addLike(noLikeFilm.getId(), user.getId());
        Film noLikedFilm = filmDbStorage.removeLike(noLikeFilm.getId(), user.getId());
        assertThat(noLikedFilm).hasFieldOrPropertyWithValue("likes", Set.of(0L));
    }

    @Test
    void testGetMostLikedFilms() {
        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        userDbStorage.createUser(user);
        Film likeFilm = new Film();
        likeFilm.setName(name);
        likeFilm.setDescription(description);
        likeFilm.setReleaseDate(releaseDate);
        likeFilm.setDuration(duration);
        filmDbStorage.createFilm(likeFilm);
        Film noLikeFilm = new Film();
        noLikeFilm.setName(name);
        noLikeFilm.setDescription(description);
        noLikeFilm.setReleaseDate(releaseDate);
        noLikeFilm.setDuration(duration);
        filmDbStorage.createFilm(noLikeFilm);
        filmDbStorage.addLike(likeFilm.getId(), user.getId());
        List<Film> mostLikedFilms = filmDbStorage.getMostLikedFilms(5);
        assertThat(mostLikedFilms.get(0)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        filmDbStorage.createFilm(film);
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName(name);
        newFilm.setDescription("updated description");
        newFilm.setReleaseDate(releaseDate);
        film.setDuration(duration);
        filmDbStorage.updateFilm(newFilm);
        Film updatedFilm = filmDbStorage.getFilm(film.getId());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "updated description");
    }
}
