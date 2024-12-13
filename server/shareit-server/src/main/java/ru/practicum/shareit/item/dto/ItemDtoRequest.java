package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoRequest {
    private Long id;
    private Long owner;
    private String name;
}
