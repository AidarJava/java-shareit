package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut addNewRequest(ItemRequestDtoIn itemRequestDtoIn);

    List<ItemRequestDtoOut> getAllYourselfRequests(Long userId);

    List<ItemRequestDtoOut> getAll();

    ItemRequestDtoOut getRequestById(Long requestId);
}
