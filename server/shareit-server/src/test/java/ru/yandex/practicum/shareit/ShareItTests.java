package ru.yandex.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ShareItTests {
    @Mock
    ItemRepository itemRepository;
    @Spy
    ItemMapper itemMapper= new ItemMapper();

    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemService;

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
}





