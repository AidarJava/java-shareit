package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item findById(long itemId);

    List<Item> findByUserId(long userId);

    Item save(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item updateItem(Item item);

    List<Item> searchItems(Long userId,String text);

    Item searchItemForAnyone(Long itemId);
}
