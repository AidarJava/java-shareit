package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;

    public ItemRepositoryImpl(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDtoOut searchItemForAnyone(Long itemId) {
        return itemMapper.mapItemToItemDtoOut(items.get(itemId));
    }

    @Override
    public ItemDtoOut findById(Long itemId) {
        return itemMapper.mapItemToItemDtoOut(items.get(itemId));
    }

    @Override
    public List<ItemDtoOut> findByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(itemMapper::mapItemToItemDtoOut)
                .toList();
    }

    @Override
    public ItemDtoOut save(ItemDtoIn itemDtoIn) {
        Item item = itemMapper.mapItemDtoInToItem(itemDtoIn);
        item.setId(findMaxItemId());
        items.put(findMaxItemId(), item);
        return itemMapper.mapItemToItemDtoOut(item);
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        Optional<Item> filtering = items.values().stream()
                .filter(item -> item.getOwner().equals(userId) && item.getId().equals(itemId))
                .findFirst();
        filtering.ifPresent(item -> items.remove(item.getId()));
    }

    @Override
    public ItemDtoOut updateItem(ItemDtoIn itemDtoIn) {
        log.info("Проверка репозитория обновления Вещи - {}", itemDtoIn);
        if (!items.containsKey(itemDtoIn.getId())) {
            throw new NotFoundException("Такой вещи нет в списке!");
        }
        Item item = items.get(itemDtoIn.getId());
        if (itemDtoIn.getDescription() != null) {
            item.setDescription(itemDtoIn.getDescription());
        }
        if (itemDtoIn.getName() != null) {
            item.setName(itemDtoIn.getName());
        }
        if (itemDtoIn.getAvailable() != null) {
            item.setAvailable(itemDtoIn.getAvailable());
        }
        items.put(itemDtoIn.getId(), item);
        return itemMapper.mapItemToItemDtoOut(item);
    }

    @Override
    public List<ItemDtoOut> searchItems(Long userId, String text) {
        return items.values().stream()
                .filter(item -> !text.isBlank() && item.getName().toLowerCase().contains(text.toLowerCase())
                        || !text.isBlank() && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(itemMapper::mapItemToItemDtoOut)
                .toList();
    }

    private long findMaxItemId() {
        long maxId = items.keySet().stream()
                .mapToLong(item -> item)
                .max()
                .orElse(0);
        return ++maxId;
    }
}

