package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemRepository {

    ItemDtoOut findById(Long itemId);

    List<ItemDtoOut> findByUserId(Long userId);

    ItemDtoOut save(ItemDtoIn itemDtoIn);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

    ItemDtoOut updateItem(ItemDtoIn itemDtoIn);

    List<ItemDtoOut> searchItems(Long userId, String text);

    ItemDtoOut searchItemForAnyone(Long itemId);
}
