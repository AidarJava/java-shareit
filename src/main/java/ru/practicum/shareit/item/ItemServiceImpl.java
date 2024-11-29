package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidRequestException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public ItemDtoOutDate searchItemForAnyone(Long itemId) {
        ItemDtoOut itemDtoOut = itemMapper.mapItemToItemDtoOut(checkItem(itemId));
        ItemDtoOutDate itemDtoOutDate = itemMapper.mapItemToItemDtoOutDate(itemDtoOut);
        List<Booking> first = bookingRepository.findNextBookingForItem(itemId, LocalDateTime.now(), Status.APPROVED);
        if (!first.isEmpty()) {
            itemDtoOutDate.setNextBooking(first.getFirst().getStart());
        }
        List<Booking> last = bookingRepository.findLastBookingForItem(itemId, LocalDateTime.now(), Status.APPROVED);
        if (!last.isEmpty()) {
            itemDtoOutDate.setLastBooking(last.getFirst().getEnd());
        }
        return itemDtoOutDate;
    }

    @Override
    public List<ItemDtoOut> getItems(Long userId) {
        return itemRepository.findAllByOwner(userId).stream()
                .map(itemMapper::mapItemToItemDtoOut)
                .toList();
    }

    @Transactional
    @Override
    public ItemDtoOut addNewItem(Long userId, ItemDtoIn itemDtoIn) {
        UserDtoOut users = userService.getUserById(userId);
        itemDtoIn.setOwner(userId);
        return itemMapper.mapItemToItemDtoOut(itemRepository.save(itemMapper.mapItemDtoInToItem(itemDtoIn)));
    }

    @Transactional
    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByOwnerAndId(userId, itemId);
    }

    @Transactional
    @Override
    public ItemDtoOut updateItem(Long userId, Long itemId, ItemDtoIn itemDtoIn) {
        ItemDtoOut itemDtoOut = itemMapper.mapItemToItemDtoOut(checkItem(itemId));
        if (!itemDtoOut.getOwner().equals(userId)) {
            throw new NotFoundException("У вас нет прав на внесение изменений!");
        }
        log.info("Проверка сервиса обновления Вещи - {}", itemDtoIn);
        ItemDtoOut oldItem = itemMapper.mapItemToItemDtoOut(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет в базе!")));
        log.info("До изменения Вещь - {}", oldItem);
        itemDtoIn.setId(itemId);
        itemDtoIn.setOwner(userId);
        if (itemDtoIn.getDescription() == null) {
            itemDtoIn.setDescription(oldItem.getDescription());
        }
        if (itemDtoIn.getName() == null) {
            itemDtoIn.setName(oldItem.getName());
        }
        if (itemDtoIn.getAvailable() == null) {
            itemDtoIn.setAvailable(oldItem.getAvailable());
        }
        return itemMapper.mapItemToItemDtoOut(itemRepository.save(itemMapper.mapItemDtoInToItem(itemDtoIn)));
    }

    @Override
    public List<ItemDtoOut> searchItems(Long userId, String text) {
        return itemRepository.searchItems(text).stream()
                .map(itemMapper::mapItemToItemDtoOut)
                .toList();
    }

    @Override
    public List<CommentDtoOut> getComments(Long userId) {
        return commentRepository.findCommentsByUserItems(userId).stream()
                .map(commentMapper::mapCommentToCommentDtoOut).toList();
    }

    @Override
    public List<CommentDtoOut> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::mapCommentToCommentDtoOut).toList();
    }

    @Transactional
    @Override
    public CommentDtoOut addNewComments(Long userId, Long itemId, CommentDtoIn commentDtoIn) {
        List<Booking> list = bookingRepository.findAllByBookerIdAndItemId(userId, itemId).stream()
                .filter(d -> d.getEnd().isBefore(LocalDateTime.now())).toList();
        if (list.isEmpty()) {
            throw new NotValidRequestException("У вас нет прав на создание комментария!");
        }
        commentDtoIn.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет в списке!")));
        commentDtoIn.setItem(checkItem(itemId));
        return commentMapper.mapCommentToCommentDtoOut(commentRepository.save(commentMapper.mapCommentDtoInToComment(commentDtoIn)));
    }

    public Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи нет в базе!"));
    }
}

