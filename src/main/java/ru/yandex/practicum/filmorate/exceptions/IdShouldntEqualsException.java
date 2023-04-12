package ru.yandex.practicum.filmorate.exceptions;

public class IdShouldntEqualsException extends RuntimeException {
    public IdShouldntEqualsException(String s) {
        super(s);
    }
}