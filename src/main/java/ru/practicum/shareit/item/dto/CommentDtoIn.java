package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class CommentDtoIn {
    Long id;
    @NotBlank
    String text;
    Item item;
    User author;
    LocalDateTime created = LocalDateTime.now();
}
