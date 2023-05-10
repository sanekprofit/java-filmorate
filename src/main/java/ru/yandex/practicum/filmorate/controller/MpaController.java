package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;
    private static final String GET_REQUEST_RECEIVED = "Получен GET запрос";

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Mpa getMpa() {
        log.info(GET_REQUEST_RECEIVED);
        return mpaService.getMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") Integer id) {
        log.info(GET_REQUEST_RECEIVED);
        return mpaService.getMpaById(id);
    }
}
