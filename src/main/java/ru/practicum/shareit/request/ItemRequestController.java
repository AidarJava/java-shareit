package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @PostMapping
    public ItemRequestDtoOut addNewRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                           @RequestBody ItemRequestDtoIn req) {
        log.info("POST/ Проверка параметров запроса метода addNewRequest, ItemRequestDtoIn - {}", req);
        return null;
    }

    @GetMapping
    public ItemRequestDtoOut getAllYourselfRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAllYourselfRequests");
        return null;
    }

    @GetMapping("/all")
    public ItemRequestDtoOut getAll(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAll");
        return null;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequestById(@RequestHeader("X-Sharer-User-Id") Long id,
                                            @PathVariable Long requestId) {
        log.info("GET/ Проверка параметров запроса метода getRequestById, requestId - {}", requestId);
        return null;
    }
}
