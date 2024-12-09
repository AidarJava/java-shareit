package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ItemRequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    public ItemRequest mapItemRequestInToItemRequest(ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequest.builder()
                .owner(itemRequestDtoIn.getOwner())
                .description(itemRequestDtoIn.getDescription())
                .created(itemRequestDtoIn.getCreated())
                .build();
    }

    public ItemRequestDtoOut mapItemRequestToItemRequestDtoOut(ItemRequest itemRequest) {
        List<ItemDtoRequest> itemsReq = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(itemMapper::mapItemToItemDtoRequest).toList();
        return ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .owner(itemRequest.getOwner())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemsReq)
                .build();
    }
}
