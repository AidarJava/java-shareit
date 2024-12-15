package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDtoIn {
    private Long id;
    private Long owner;
    private String url;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}