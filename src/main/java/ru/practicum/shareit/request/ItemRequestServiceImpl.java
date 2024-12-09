package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    @Override
    public ItemRequestDtoOut addNewRequest(Long userId, ItemRequestDtoIn itemRequestDtoIn) {
        itemRequestDtoIn.setOwner(userId);
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
                itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Такого запроса нет в базе!")));
    }
}
