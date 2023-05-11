package ru.yandex.practicum.filmorate.exceptions;

public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException(String s) {
        super(s);
    }
}
