package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {

    List<ItemDtoOut> getItems(long userId);

    ItemDtoOut addNewItem(long userId, ItemDtoIn itemDtoIn);

    void deleteItem(long userId, long itemId);

    ItemDtoOut updateItem(Long userId,Long itemId,ItemDtoIn itemDtoIn);

    List<ItemDtoOut> searchItems(Long userId,String text);

    ItemDtoOut searchItemForAnyone(Long itemId);
}
