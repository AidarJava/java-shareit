package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<ItemDtoOut> getItems(Long userId);

    ItemDtoOut addNewItem(Long userId, ItemDtoIn itemDtoIn);

    void deleteItem(Long userId, Long itemId);

    ItemDtoOut updateItem(Long userId, Long itemId, ItemDtoIn itemDtoIn);

    List<ItemDtoOut> searchItems(Long userId, String text);

    ItemDtoOutDate searchItemForAnyone(Long itemId);

    List<CommentDtoOut> getComments(Long userId);

    List<CommentDtoOut> getCommentsByItemId(Long itemId);

    CommentDtoOut addNewComments(Long userId, Long itemId, CommentDtoIn commentDtoIn);

}
