package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
    @Autowired
    UserController uc;
    @Autowired
    FilmController fc;
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
        Film film = new Film();
        film.setId(1);
        film.setName(name);
        film.setDescription("very looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ng description");
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
}
