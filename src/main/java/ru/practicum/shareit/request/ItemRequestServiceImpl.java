package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    ItemRequestMapper itemRequestMapper;
    @Override
    public ItemRequestDtoOut addNewRequest(ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestMapper.mapItemRequestToItemRequestDtoOut(itemRequestRepository.save(itemRequestMapper.mapItemRequestInToItemRequest(itemRequestDtoIn)));
    }

    @Override
    public List<ItemRequestDtoOut> getAllYourselfRequests(Long userId) {
        return itemRequestRepository.findAllByOwner(userId).stream()
                .map(itemRequestMapper::mapItemRequestToItemRequestDtoOut).toList();
    }

    @Override
    public List<ItemRequestDtoOut> getAll() {
        return itemRequestRepository.findAll().stream().map(itemRequestMapper::mapItemRequestToItemRequestDtoOut).toList();
    }

    @Override
    public ItemRequestDtoOut getRequestById(Long requestId) {
        return itemRequestMapper.mapItemRequestToItemRequestDtoOut(
                itemRequestRepository.findById(requestId).orElseThrow(()->new NotFoundException("Такого запроса нет в базе!")));
    }
}
