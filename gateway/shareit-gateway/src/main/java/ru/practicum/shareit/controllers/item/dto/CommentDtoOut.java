package ru.practicum.shareit.controllers.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDtoOut {
    Long id;
    String text;
    ItemDtoOut item;
    String authorName;
    LocalDateTime created;
}
