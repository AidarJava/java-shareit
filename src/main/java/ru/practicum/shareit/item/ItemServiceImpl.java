package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserServiceImpl userService;

    @Override
    public ItemDtoOut searchItemForAnyone(Long itemId) {
        return itemMapper.mapItemToItemDtoOut(itemRepository.searchItemForAnyone(itemId));
    }

    @Override
    public List<ItemDtoOut> getItems(long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(itemMapper::mapItemToItemDtoOut)
                .toList();
    }

    @Override
    public ItemDtoOut addNewItem(long userId, ItemDtoIn itemDtoIn) {
        UserDtoOut users = userService.getUserById(userId);
        Item item = itemMapper.mapItemDtoInToItem(itemDtoIn);
        item.setOwner(userId);
        return itemMapper.mapItemToItemDtoOut(itemRepository.save(item));
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDtoOut updateItem(Long userId, Long itemId, ItemDtoIn itemDtoIn) {
        if (itemRepository.findById(itemId).getOwner() != userId) {
            throw new NotFoundException("У вас нет прав на внесение изменений!");
        }
        log.info("Проверка сервиса обновления Вещи - {}", itemDtoIn);
        Item item = itemMapper.mapItemDtoInToItem(itemDtoIn);
        log.info("Проверка маппера в сервисе обновления Вещи - {}", item);
        item.setId(itemId);
        return itemMapper.mapItemToItemDtoOut(itemRepository.updateItem(item));
    }

    @Override
    public List<ItemDtoOut> searchItems(Long userId, String text) {
        return itemRepository.searchItems(userId, text).stream()
                .map(itemMapper::mapItemToItemDtoOut)
                .toList();
    }
}

