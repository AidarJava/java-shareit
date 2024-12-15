package ru.yandex.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOutDate;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ShareItControllerTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDtoOut userDtoOut;
    private UserDtoIn userDtoIn;
    private ItemDtoIn itemDtoIn;
    private ItemDtoOut itemDtoOut;
    private ItemDtoOutDate itemDtoOutDate;
    private BookingDtoOut bookingDtoOut;
    private BookingDtoIn bookingDtoIn;
    private ItemRequestDtoIn itemRequestDtoIn;
    private ItemRequestDtoOut itemRequestDtoOut;

    @BeforeEach
    void setUp() {

        userDtoIn = new UserDtoIn();
        userDtoIn.setEmail("yandex@mail.com");
        userDtoIn.setName("Роман");

        userDtoOut = new UserDtoOut(1L, "yandex@mail.com", "Роман");

        itemDtoIn = new ItemDtoIn();
        itemDtoIn.setDescription("Хорошая вещь");
        itemDtoIn.setName("Вещь");
        itemDtoIn.setAvailable(true);

        itemDtoOut = new ItemDtoOut(1L, userDtoOut.getId(), null, "Хорошая вещь", "Вещь", true);

        itemDtoOutDate = new ItemDtoOutDate(1L, userDtoOut.getId(), null, "Хорошая вещь", "Вещь", true, null, null, null);

        bookingDtoIn = new BookingDtoIn();
        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        User user = new User(1L, "yandex@mail.com", "Роман");
        Item item = new Item();
        item.setId(1L);
        item.setDescription("Хорошая вещь");
        item.setName("Вещь");
        item.setAvailable(true);
        bookingDtoIn.setStart(start);
        bookingDtoIn.setEnd(end);
        bookingDtoIn.setBooker(user);
        bookingDtoIn.setItemId(itemDtoOut.getId());
        bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setStart(start);
        bookingDtoOut.setEnd(end);
        bookingDtoOut.setBooker(user);
        bookingDtoOut.setItem(item);

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setOwner(userDtoIn.getId());
        itemRequestDtoIn.setDescription("Запрос");
        itemRequestDtoIn.setCreated(LocalDateTime.now());
        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(1L, 1L, "New");
        List<ItemDtoRequest> list = List.of(itemDtoRequest);
        itemRequestDtoOut = new ItemRequestDtoOut(1L, userDtoIn.getId(), "Запрос", LocalDateTime.now(), list);
    }

    @SneakyThrows
    @Test
    void saveNewUser() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
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
    void addNewBooking() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        when(bookingService.addNewBooking(any(), any()))
                .thenReturn(bookingDtoOut);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userDtoOut.getId())
                        .content(mapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(Arrays.asList(2025, 4, 8, 12, 30))))
                .andExpect(jsonPath("$.end", is(Arrays.asList(2026, 4, 8, 12, 30))));
    }

    @Test
    void bookingConformation() {
        when(bookingService.bookingConformation(any(), any(), anyBoolean()))
                .thenReturn(bookingDtoOut);
        BookingDtoOut response = bookingController.bookingConformation(any(), any(), anyBoolean());
        assertEquals(bookingDtoOut, response);
    }

    @Test
    void checkBookingStatus() {
        when(bookingService.checkBookingStatus(any(), any()))
                .thenReturn(bookingDtoOut);
        BookingDtoOut response = bookingController.checkBookingStatus(any(), any());
        assertEquals(bookingDtoOut, response);
    }

    @Test
    void findBookingsByUser() {
        List<BookingDtoOut> bookings = List.of(new BookingDtoOut());
        when(bookingService.findBookingsByUser(any(), any()))
                .thenReturn(bookings);
        List<BookingDtoOut> response = bookingController.findBookingsByUser(any(), anyString());
        assertEquals(bookings, response);
    }

    @Test
    void findBookingItemsByUser() {
        List<BookingDtoOut> bookings = List.of(new BookingDtoOut());
        when(bookingService.findBookingItemsByUser(any(), any()))
                .thenReturn(bookings);
        List<BookingDtoOut> response = bookingController.findBookingItemsByUser(any(), anyString());
        assertEquals(bookings, response);
    }

    @Test
    void updateUser() {
        when(userService.updateUser(userDtoIn.getId(), userDtoIn)).thenReturn(userDtoOut);
        UserDtoOut response = userController.updateUser(userDtoIn.getId(), userDtoIn);
        assertEquals(userDtoOut, response);
    }

    @Test
    void deleteUserById() {
        ResponseEntity<String> responseEntity = ResponseEntity.ok("Удаление прошло успешно!");
        when(userService.deleteUserById(userDtoIn.getId())).thenReturn(responseEntity);
        ResponseEntity<String> response = userController.deleteUserById(any());
        assertEquals(responseEntity, response);
    }

    @Test
    void getAllUsers() {
        List<UserDtoOut> users = List.of(new UserDtoOut());
        when(userService.getAllUsers()).thenReturn(users);
        List<UserDtoOut> response = userController.getAllUsers();
        assertEquals(users, response);
    }

    @SneakyThrows
    @Test
    void saveNewItem() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        when(itemService.addNewItem(userDtoOut.getId(), itemDtoIn))
                .thenReturn(itemDtoOut);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userDtoOut.getId())
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())));
    }

    @Test
    void searchItems() {
        List<ItemDtoOut> items = List.of(new ItemDtoOut());
        when(itemService.searchItems(any(), any())).thenReturn(items);
        List<ItemDtoOut> response = itemController.searchPrivate(any(), any());
        assertEquals(items, response);
    }

    @Test
    void getAllItems() {
        List<ItemDtoOut> items = List.of(new ItemDtoOut());
        when(itemService.getItems(any())).thenReturn(items);
        List<ItemDtoOut> response = itemController.get(any());
        assertEquals(items, response);
    }

    @SneakyThrows
    @Test
    void getUserById() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
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
    void getItemById() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        Long itemId = 1L;
        when(itemService.searchItemForAnyone(itemId)).thenReturn(itemDtoOutDate);
        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userDtoOut.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOutDate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOutDate.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOutDate.getDescription())));
    }

    @SneakyThrows
    @Test
    void saveUserCheckValidUser() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        UserDtoIn user = new UserDtoIn();
        user.setEmail("jsghksjgf");
        user.setName("Коля");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).saveUser(user);
    }

    @Test
    void addNewRequest() {
        when(itemRequestService.addNewRequest(userDtoIn.getId(), itemRequestDtoIn)).thenReturn(itemRequestDtoOut);
        ItemRequestDtoOut response = itemRequestController.addNewRequest(userDtoIn.getId(), itemRequestDtoIn);
        assertEquals(itemRequestDtoOut, response);
    }

    @Test
    void getAllYourselfRequests() {
        when(itemRequestService.getAllYourselfRequests(userDtoIn.getId())).thenReturn(List.of(itemRequestDtoOut));
        List<ItemRequestDtoOut> response = itemRequestController.getAllYourselfRequests(userDtoIn.getId());
        assertEquals(List.of(itemRequestDtoOut), response);
    }

    @Test
    void getAll() {
        when(itemRequestService.getAll()).thenReturn(List.of(itemRequestDtoOut));
        List<ItemRequestDtoOut> response = itemRequestController.getAll(userDtoIn.getId());
        assertEquals(List.of(itemRequestDtoOut), response);
    }

    @Test
    void getRequestById() {
        when(itemRequestService.getRequestById(userDtoIn.getId())).thenReturn(itemRequestDtoOut);
        ItemRequestDtoOut response = itemRequestController.getRequestById(userDtoIn.getId(), itemRequestDtoIn.getId());
        assertEquals(itemRequestDtoOut, response);
    }

    @Test
    void updateItem() {
        when(itemService.updateItem(any(), any(), any())).thenReturn(itemDtoOut);
        ItemDtoOut response = itemController.update(userDtoIn.getId(), itemDtoOut.getId(), itemDtoIn);
        assertEquals(itemDtoOut, response);
    }
}


