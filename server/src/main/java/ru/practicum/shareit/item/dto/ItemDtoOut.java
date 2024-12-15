package ru.practicum.shareit.item.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOut {
    private Long id;
    private Long owner;
    private String url;
    private String name;
    private String description;
    private Boolean available;
}
