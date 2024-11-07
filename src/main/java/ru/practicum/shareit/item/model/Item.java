package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    @NotBlank
    private Long id;
    @Positive
    private Long owner;
    private String url;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
}