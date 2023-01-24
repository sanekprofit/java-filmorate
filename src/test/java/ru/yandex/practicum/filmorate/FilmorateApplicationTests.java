package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
    boolean thrown;
    ValidationCheck validationCheck = new ValidationCheck();
    int duration = 100;
    String login = "login";
    String name = "name";
    String email = "mail@mail.com";
    String description = "description";
    LocalDate releaseDate = LocalDate.of(2000, 5, 12);
    LocalDate birthDay = LocalDate.of(1970, 11, 28);

    @BeforeEach
    void beforeEach() {
        thrown = false;
    }

    @Test
    void emptyUserNameShouldSetLoginForName() {
        User user = new User();
        user.setId(1);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(birthDay);
        validationCheck.validationCheckUser(user);
        assertEquals("login", user.getName());
    }

    @Test
    void invalidUserEmailShouldReturnValidationException() {
        User user = new User();
        user.setId(1);
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(LocalDate.MAX);
        try {
            validationCheck.validationCheckUser(user);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void invalidUserBirthDayShouldReturnValidationException() {
        User user = new User();
        user.setId(1);
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setBirthday(LocalDate.MAX);
        try {
            validationCheck.validationCheckUser(user);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void emptyUserLoginShouldReturnValidationException() {
        User user = new User();
        user.setId(1);
        user.setName(name);
        user.setEmail(email);
        user.setBirthday(birthDay);
        try {
            validationCheck.validationCheckUser(user);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void emptyFilmNameShouldReturnValidationException() {
        Film film = new Film();
        film.setId(1);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        try {
            validationCheck.validationCheckFilm(film);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(thrown);
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
        try {
            validationCheck.validationCheckFilm(film);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void invalidFilmReleaseDateShouldReturnValidationException() {
        Film film = new Film();
        film.setId(1);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.MIN);
        film.setDuration(duration);
        try {
            validationCheck.validationCheckFilm(film);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(true);
    }

    @Test
    void negativeFilmDurationShouldReturnValidationException() {
        Film film = new Film();
        film.setId(1);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(-1);
        try {
            validationCheck.validationCheckFilm(film);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(true);
    }
}
