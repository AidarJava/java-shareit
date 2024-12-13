package ru.yandex.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.user.dto.UserDtoIn;

import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@SpringBootTest(classes = ShareItServer.class)
public class ShareItJsonTests {
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void testValidUserJson() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        String validJson = """
                {
                  "name": "Ваня",
                  "email": "good@mail.com"
                }
                """;
        UserDtoIn userDtoIn = objectMapper.readValue(validJson, UserDtoIn.class);
        Set<ConstraintViolation<UserDtoIn>> violations = validator.validate(userDtoIn);
        assertTrue(violations.isEmpty(), "Ожидалось отсутсвие нарушений");
    }

    @SneakyThrows
    @Test
    void testValidItemJson() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        String validJson = """
                {
                  "name": "Вещь",
                  "description": "Описание вещи",
                  "available": true
                }
                """;
        ItemDtoIn itemDtoIn = objectMapper.readValue(validJson, ItemDtoIn.class);
        Set<ConstraintViolation<ItemDtoIn>> violations = validator.validate(itemDtoIn);
        assertTrue(violations.isEmpty(), "Ожидалось отсутсвие нарушений");
    }
}
