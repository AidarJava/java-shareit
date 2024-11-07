package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Item searchItemForAnyone(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .toList();
    }

    @Override
    public Item save(Item item) {
        item.setId(findMaxItemId());
        items.put(findMaxItemId(), item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        Optional<Item> filtering = findByUserId(userId).stream()
                .filter(item -> item.getOwner().equals(userId) && item.getId().equals(itemId))
                .findFirst();
        filtering.ifPresent(item -> items.remove(item.getId()));
    }

    @Override
    public Item updateItem(Item itemIn) {
        log.info("Проверка репозитория обновления Вещи - {}", itemIn);
        if (!items.containsKey(itemIn.getId())) {
            throw new NotFoundException("Такой вещи нет в списке!");
        }
        Item item = items.get(itemIn.getId());
        if (itemIn.getDescription() != null) {
            item.setDescription(itemIn.getDescription());
        }
        if (itemIn.getName() != null) {
            item.setName(itemIn.getName());
        }
        if (itemIn.getAvailable() != null) {
            item.setAvailable(itemIn.getAvailable());
        }
        items.put(itemIn.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        return items.values().stream()
                .filter(item -> !text.isBlank() && item.getName().toLowerCase().contains(text.toLowerCase())
                        || !text.isBlank() && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
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

