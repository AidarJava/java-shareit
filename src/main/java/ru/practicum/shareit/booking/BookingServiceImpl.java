package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidOwner;
import ru.practicum.shareit.exception.NotValidRequestException;
import ru.practicum.shareit.exception.ServerErrorException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDtoOut addNewBooking(Long userId, BookingDtoIn booking) {
        log.info("Проверка сервиса создания бронирования - {}", booking.getItemId());
        User user = checkUser(userId);
        Item findItem = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Такой вещи нет в базе!"));
        if (!findItem.getAvailable()) {
            throw new NotValidRequestException("Запрос некорректно сформирован! Эта вешь недоступна!");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new NotValidRequestException("Запрос некорректно сформирован! Время начала и окончания совпадают!");
        }
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        log.info("Проверка сервиса создания бронирования перед этапом сохраненя - {}", booking);
        return bookingMapper.mapBookingToBookingDtoOut(bookingRepository.save(bookingMapper.mapBookingDtoInToBooking(booking)));
    }

    @Override
    public BookingDtoOut bookingConformation(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Такого бронирования нет в базе!"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner())) {
            throw new NotValidOwner("Вы не можете подтвердить или отклонить запрос на бронирование!");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            if (userId.equals(booking.getBooker().getId())) {
                booking.setStatus(Status.CANCELED);
            }
            if (userId.equals(booking.getItem().getOwner())) {
                booking.setStatus(Status.REJECTED);
            }
        }
        return bookingMapper.mapBookingToBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut checkBookingStatus(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Такого бронирования нет в базе!"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner())) {
            throw new NotValidOwner("У вас недостаточно прав!");
        }
        return bookingMapper.mapBookingToBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> findBookingsByUser(Long userId, String state) {
        User user = checkUser(userId);
        return switch (state) {
            case "ALL" -> bookingRepository.findAllByBookerId(userId).stream()
                    .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "CURRENT" -> bookingRepository.searchCurrentBookings(userId, LocalDateTime.now()).stream()
                    .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "PAST" -> bookingRepository.searchPastBookings(userId, LocalDateTime.now()).stream()
                    .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "FUTURE" -> bookingRepository.searchFutureBookings(userId, LocalDateTime.now()).stream()
                    .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "WAITING" ->
                    bookingRepository.findByBookerIdAndStatus(userId, Status.WAITING, Sort.by(Sort.Order.asc("start"))).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "REJECTED" ->
                    bookingRepository.findByBookerIdAndStatus(userId, Status.REJECTED, Sort.by(Sort.Order.asc("start"))).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            default -> null;
        };
    }

    @Override
    public List<BookingDtoOut> findBookingItemsByUser(Long userId, String state) {
        User user = checkUser(userId);
        return switch (state) {
            case "ALL" ->
                    bookingRepository.searchAllBookingsByUserItems(userId, itemRepository.findAllByOwner(userId)).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut)
                            .toList();
            case "CURRENT" ->
                    bookingRepository.searchAllCurrentBookingsByUserItems(userId, LocalDateTime.now(), itemRepository.findAllByOwner(userId)).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "PAST" ->
                    bookingRepository.searchAllPastBookingsByUserItems(userId, LocalDateTime.now(), itemRepository.findAllByOwner(userId)).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "FUTURE" ->
                    bookingRepository.searchAllFutureBookingsByUserItems(userId, LocalDateTime.now(), itemRepository.findAllByOwner(userId)).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "WAITING" ->
                    bookingRepository.searchAllStatusBookingsByUserItems(userId, Status.WAITING, itemRepository.findAllByOwner(userId)).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            case "REJECTED" ->
                    bookingRepository.searchAllStatusBookingsByUserItems(userId, Status.REJECTED, itemRepository.findAllByOwner(userId)).stream()
                            .map(bookingMapper::mapBookingToBookingDtoOut).toList();
            default -> null;
        };

    }

    public User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException("Запрос некорректно сформирован! Такого пользователя нет в базе!"));
    }
}
