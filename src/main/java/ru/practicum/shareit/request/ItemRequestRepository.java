package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOwnerOrderByCreatedDesc(Long userId);

    List<ItemRequest> findAllOrderByCreatedDesc();
}
