package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequestDtoIn {
    private Long id;
    private Long owner;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
