package ru.practicum.shareit.controllers.itemRequest.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.controllers.item.dto.ItemDtoRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDtoOut {
    private Long id;
    private Long owner;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoRequest> items;
}
