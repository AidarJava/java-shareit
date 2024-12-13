package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;


@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentRepository commentRepository;

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

    public ItemDtoOutDate mapItemToItemDtoOutDate(ItemDtoOut item) {
        return ItemDtoOutDate.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .url(item.getUrl())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(commentRepository.findAllByItemId(item.getId()).stream().map(Comment::getText).toList())
                .build();
    }
}
