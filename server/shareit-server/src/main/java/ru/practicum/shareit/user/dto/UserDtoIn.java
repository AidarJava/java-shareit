package ru.practicum.shareit.user.dto;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDtoIn {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
