package ru.yandex.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidOwner;
import ru.practicum.shareit.exception.NotValidRequestException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShareItTests {
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRepository itemRepository;
    @Spy
    ItemMapper itemMapper = new ItemMapper(commentRepository);
    @Mock
    BookingMapper bookingMapper;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl itemService;
    @InjectMocks
    BookingServiceImpl bookingService;
    @Captor
    ArgumentCaptor<Booking> bookingArgumentCaptor;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @Captor
    private ArgumentCaptor<Item> argumentCaptor;

    @SneakyThrows
    @Test
    void getItemByIdException() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.searchItemForAnyone(itemId));
    }

    @Test
    void updateItem_shouldUpdateAndSaveItemSuccessfully() {
        // Исходные данные
        Long userId = 1L;
        Long itemId = 1L;

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(userId);
        existingItem.setName("Old Item");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(true);

        ItemDtoIn updatedItemDto = new ItemDtoIn();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setAvailable(false);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setOwner(userId);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        updatedItem.setAvailable(false);

        // Заглушки для itemRepository
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        // Выполнение метода
        ItemDtoOut result = itemService.updateItem(userId, itemId, updatedItemDto);

        // Проверка результата
        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Updated Item", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.getAvailable());

        // Проверка вызовов
        verify(itemRepository, times(2)).findById(itemId); // Учёт двух вызовов
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundExceptionIfItemDoesNotExist() {
        // Данные
        Long userId = 1L;
        Long itemId = 1L;
        ItemDtoIn itemDtoIn = new ItemDtoIn();

        // Заглушка для itemRepository
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Ожидаемое исключение
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(userId, itemId, itemDtoIn));

        assertEquals("Такой вещи нет в базе!", exception.getMessage());

        // Проверка вызовов
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_shouldThrowNotFoundExceptionIfUserIsNotOwner() {
        // Данные
        Long userId = 2L; // Другой пользователь
        Long itemId = 1L;

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setOwner(1L); // Владелец другой

        ItemDtoIn itemDtoIn = new ItemDtoIn();

        // Заглушка для itemRepository
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Ожидаемое исключение
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.updateItem(userId, itemId, itemDtoIn));

        assertEquals("У вас нет прав на внесение изменений!", exception.getMessage());

        // Проверка вызовов
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void addNewBookingGetAvailableException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("Ivan");


        Item item = new Item();
        item.setId(1L);
        item.setOwner(1L);
        item.setName("Вещь");
        item.setDescription("Новая вещь");
        item.setAvailable(false);

        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);

        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(start);
        bookingDtoIn.setEnd(end);
        bookingDtoIn.setItemId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThrows(NotValidRequestException.class, () -> bookingService.addNewBooking(1L, bookingDtoIn));
    }

    @Test
    void addNewBookingEndStartException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("Ivan");


        Item item = new Item();
        item.setId(1L);
        item.setOwner(1L);
        item.setName("Вещь");
        item.setDescription("Новая вещь");
        item.setAvailable(true);

        String str1 = "2025-04-08 12:30";
        String str2 = "2025-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);

        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(start);
        bookingDtoIn.setEnd(end);
        bookingDtoIn.setItemId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThrows(NotValidRequestException.class, () -> bookingService.addNewBooking(1L, bookingDtoIn));
    }

    @Test
    void bookingConformationCheckStatus() {
        User user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("Ivan");


        Item item = new Item();
        item.setId(1L);
        item.setOwner(1L);
        item.setName("Вещь");
        item.setDescription("Новая вещь");
        item.setAvailable(true);

        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);

        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(start);
        bookingDtoIn.setEnd(end);
        bookingDtoIn.setItemId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);

        BookingDtoOut booking1 = new BookingDtoOut();
        booking1.setId(booking.getId());
        booking1.setStart(booking.getStart());
        booking1.setEnd(booking.getEnd());
        booking1.setItem(booking.getItem());
        booking1.setBooker(booking.getBooker());
        when(bookingMapper.mapBookingToBookingDtoOut(any())).thenReturn(booking1);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        BookingDtoOut bookingDtoOut = bookingService.bookingConformation(user.getId(), 1L, false);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        assertEquals(bookingArgumentCaptor.getValue().getStatus(), Status.REJECTED);
    }

    @Test
    void bookingConformationCheckStatusApproved() {
        User user = new User();
        user.setId(1L);
        user.setEmail("mail@mail.ru");
        user.setName("Ivan");


        Item item = new Item();
        item.setId(1L);
        item.setOwner(1L);
        item.setName("Вещь");
        item.setDescription("Новая вещь");
        item.setAvailable(true);

        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);

        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(start);
        bookingDtoIn.setEnd(end);
        bookingDtoIn.setItemId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);

        BookingDtoOut booking1 = new BookingDtoOut();
        booking1.setId(booking.getId());
        booking1.setStart(booking.getStart());
        booking1.setEnd(booking.getEnd());
        booking1.setItem(booking.getItem());
        booking1.setBooker(booking.getBooker());
        when(bookingMapper.mapBookingToBookingDtoOut(any())).thenReturn(booking1);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        BookingDtoOut bookingDtoOut = bookingService.bookingConformation(user.getId(), 1L, true);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        assertEquals(bookingArgumentCaptor.getValue().getStatus(), Status.APPROVED);
    }

    @Test
    void bookingConformationAndStatusException() {
        User user = new User();
        user.setId(2L);
        user.setEmail("mail@mail.ru");
        user.setName("Ivan");

        Item item = new Item();
        item.setId(1L);
        item.setOwner(3L);
        item.setName("Вещь");
        item.setDescription("Новая вещь");
        item.setAvailable(true);

        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);

        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        bookingDtoIn.setStart(start);
        bookingDtoIn.setEnd(end);
        bookingDtoIn.setItemId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        assertThrows(NotValidOwner.class, () -> bookingService.bookingConformation(1L, 1L, true));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        assertThrows(NotValidOwner.class, () -> bookingService.checkBookingStatus(1L, 1L));
    }
}





