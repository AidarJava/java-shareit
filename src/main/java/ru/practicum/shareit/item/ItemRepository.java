package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(Long userId);

    void deleteByOwnerAndId(Long userId, Long itemId);

    @Query("select i from Item as i " +
            "where i.available =true and (upper(i.description) like upper(?1) or upper(i.name) like upper(?1))")
    List<Item> searchItems(String text);

}
