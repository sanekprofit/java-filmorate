package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Set<Long> likes = new HashSet<>();
    private int duration;
    private Set<Genre> genres = new LinkedHashSet<>();
    private Mpa mpa;
}