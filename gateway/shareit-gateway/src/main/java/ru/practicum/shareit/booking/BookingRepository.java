package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long userId);

    @Query("select b from Booking as b where b.booker.id=?1 and b.start < ?2 and b.end > ?2 order by b.start ASC")
    List<Booking> searchCurrentBookings(Long userId, LocalDateTime time);

    @Query("select b from Booking as b where b.booker.id=?1 and b.end < ?2 order by b.start ASC")
    List<Booking> searchPastBookings(Long userId, LocalDateTime time);

    @Query("select b from Booking as b where b.booker.id=?1 and b.start > ?2 order by b.start ASC")
    List<Booking> searchFutureBookings(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatus(Long userId, Status status, Sort sort);

    @Query("select b from Booking as b where b.booker.id=?1 and b.item in ?2 order by b.start ASC")
    List<Booking> searchAllBookingsByUserItems(Long userId, List<Item> item);

    @Query("select b from Booking as b where b.booker.id=?1 and b.start < ?2 and b.end > ?2 and b.item in ?3 " +
            "order by b.start ASC ")
    List<Booking> searchAllCurrentBookingsByUserItems(Long userId, LocalDateTime time, List<Item> item);

    @Query("select b from Booking as b where b.booker.id=?1 and b.end < ?2 and b.item in ?3 " +
            "order by b.start ASC ")
    List<Booking> searchAllPastBookingsByUserItems(Long userId, LocalDateTime time, List<Item> item);

    @Query("select b from Booking as b where b.booker.id=?1 and b.start > ?2 and b.item in ?3 " +
            "order by b.start ASC ")
    List<Booking> searchAllFutureBookingsByUserItems(Long userId, LocalDateTime time, List<Item> item);

    @Query("select b from Booking as b where b.booker.id=?1 and b.status > ?2 and b.item in ?3 " +
            "order by b.start ASC ")
    List<Booking> searchAllStatusBookingsByUserItems(Long userId, Status status, List<Item> item);

    List<Booking> findAllByBookerIdAndItemId(Long userId, Long itemId);

    @Query("select b from Booking as b where b.item.id=?1 and b.end < ?2 and b.status=?3 " +
            "order by b.end DESC ")
    List<Booking> findLastBookingForItem(Long itemId, LocalDateTime localDateTime, Status status);

    @Query("select b from Booking as b where b.item.id=?1 and b.start > ?2 and b.status=?3 " +
            "order by b.end ASC ")
    List<Booking> findNextBookingForItem(Long itemId, LocalDateTime localDateTime, Status status);

}
