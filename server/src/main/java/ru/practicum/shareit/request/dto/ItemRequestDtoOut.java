package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDtoOut {
    private Long id;
    private Long owner;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoRequest> items;
}
