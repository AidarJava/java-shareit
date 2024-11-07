package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item findById(Long itemId);

    List<Item> findByUserId(Long userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(Long userId, Long itemId);

    Item updateItem(Item item);

    List<Item> searchItems(Long userId,String text);

    Item searchItemForAnyone(Long itemId);
}
