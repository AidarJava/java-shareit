package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public ItemDtoOut searchItemForAnyone(Long itemId) {
        return itemRepository.searchItemForAnyone(itemId);
    }

    @Override
    public List<ItemDtoOut> getItems(Long userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public ItemDtoOut addNewItem(Long userId, ItemDtoIn itemDtoIn) {
        UserDtoOut users = userService.getUserById(userId);
        itemDtoIn.setOwner(userId);
        return itemRepository.save(itemDtoIn);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDtoOut updateItem(Long userId, Long itemId, ItemDtoIn itemDtoIn) {
        if (!itemRepository.findById(itemId).getOwner().equals(userId)) {
            throw new NotFoundException("У вас нет прав на внесение изменений!");
        }
        log.info("Проверка сервиса обновления Вещи - {}", itemDtoIn);
        itemDtoIn.setId(itemId);
        return itemRepository.updateItem(itemDtoIn);
    }

    @Override
    public List<ItemDtoOut> searchItems(Long userId, String text) {
        return itemRepository.searchItems(userId, text);
    }
}

