package ru.practicum.shareit.controllers.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.controllers.user.dto.UserDtoIn;

import java.time.LocalDateTime;

@Data
public class CommentDtoIn {
    Long id;
    @NotBlank
    String text;
    ItemDtoIn item;
    UserDtoIn author;
    LocalDateTime created = LocalDateTime.now();
}
