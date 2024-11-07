package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public Item mapItemDtoInToItem(ItemDtoIn itemDtoIn) {
        return Item.builder()
                .owner(itemDtoIn.getOwner())
                .url(itemDtoIn.getUrl())
                .name(itemDtoIn.getName())
                .description(itemDtoIn.getDescription())
                .available(itemDtoIn.getAvailable())
                .build();
    }

    public ItemDtoOut mapItemToItemDtoOut(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .url(item.getUrl())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}
