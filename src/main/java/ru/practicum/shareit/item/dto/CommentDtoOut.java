package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
public class CommentDtoOut {
    Long id;
    String text;
    Item item;
    String authorName;
    LocalDateTime created;
}
