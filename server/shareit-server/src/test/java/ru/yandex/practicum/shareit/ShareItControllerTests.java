package ru.yandex.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShareItControllerTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDtoOut userDtoOut;
    private UserDtoIn userDtoIn;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        userDtoIn = new UserDtoIn();
        userDtoIn.setEmail("yandex@mail.com");
        userDtoIn.setName("Роман");

        userDtoOut = new UserDtoOut(
                1L,
                "yandex@mail.com",
                "Роман");
    }

    @SneakyThrows
    @Test
    void saveNewUser() {
        when(userService.saveUser(any()))
                .thenReturn(userDtoOut);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail())));
    }

    @SneakyThrows
    @Test
    void getUserById() {
        Long userId = 1L;
        //UserDtoOut userDtoOut = new UserDtoOut(1,"john.doe@mail.com","John");
        when(userService.getUserById(userId)).thenReturn(userDtoOut);
        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOut.getEmail())));
    }

    @SneakyThrows
    @Test
    void saveUserCheckValidUser() {
        UserDtoIn user = new UserDtoIn();
        user.setEmail("jsghksjgf");
        user.setName("Коля");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(user);
    }
}


