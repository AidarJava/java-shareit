package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut addNewRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                           @RequestBody ItemRequestDtoIn req) {
        log.info("POST/ Проверка параметров запроса метода addNewRequest, ItemRequestDtoIn - {}", req);
        return itemRequestService.addNewRequest(id, req);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAllYourselfRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAllYourselfRequests");
        return itemRequestService.getAllYourselfRequests(id);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAll(@RequestHeader("X-Sharer-User-Id") Long id) {
        log.info("GET/ Проверка вызова метода getAll");
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequestById(@RequestHeader("X-Sharer-User-Id") Long id,
                                            @PathVariable Long requestId) {
        log.info("GET/ Проверка параметров запроса метода getRequestById, requestId - {}", requestId);
        return itemRequestService.getRequestById(requestId);
    }
}
