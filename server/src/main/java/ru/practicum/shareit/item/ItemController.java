package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoOut> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET/ Проверка параметров запроса метода get, userId - {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> searchPrivate(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(name = "text") String text) {
        log.info("GET/ Проверка параметров запроса метода searchPrivate, userId - {} text - {} ", userId, text);
        return itemService.searchItems(userId, text);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOutDate search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId) {
        log.info("GET/ Проверка параметров запроса метода search, userId - {} itemId - {} ", userId, itemId);
        return itemService.searchItemForAnyone(itemId);
    }

    @PostMapping
    public ItemDtoOut add(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDtoIn itemDtoIn) {
        log.info("POST/ Проверка параметров запроса метода add, userId - {} ItemDtoIn - {} ", userId, itemDtoIn);
        return itemService.addNewItem(userId, itemDtoIn);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable(name = "itemId") Long itemId,
                             @RequestBody ItemDtoIn itemDtoIn) {
        log.info("PATCH/ Проверка параметров запроса метода update, userId - {}, itemId - {},ItemDtoId - {}", userId, itemId, itemDtoIn);
        return itemService.updateItem(userId, itemId, itemDtoIn);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(name = "itemId") Long itemId) {
        log.info("DELETE/ Проверка параметров запроса метода deleteItem, userId - {}, itemId - {} ", userId, itemId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable(name = "itemId") Long itemId,
                                    @RequestBody CommentDtoIn commentDtoIn) {
        log.info("POST/ Проверка параметров запроса метода addComment, userId - {} ItemId - {} comment {}", userId, itemId, commentDtoIn);
        return itemService.addNewComments(userId, itemId, commentDtoIn);
    }
}
