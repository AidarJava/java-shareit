package ru.yandex.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        classes = ShareItServer.class,
        properties = "spring.datasource.url=jdbc:postgresql://localhost:5433/shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ShareItServiceImplTests {
    private final EntityManager entityManager;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestServiceImpl itemRequestService;

    @Test
    void testSaveUser() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut=userService.saveUser(userDto);
       // entityManager.flush();
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    private UserDtoIn makeUserDto(String email, String name) {
        UserDtoIn dto = new UserDtoIn();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
    @Test
    void testGetUserItems() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");

        UserDtoOut userDtoOut=userService.saveUser(userDto);
        //entityManager.flush();
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество",false);
        ItemDtoOut itemDtoOut=itemService.addNewItem(user.getId(),itemDto);
       // entityManager.flush();
        ItemDtoOut item = makeItemDtoOut(itemDtoOut.getId(),userDtoOut.getId(),null,"Вещь", "Хорошее качество",false);
        List<ItemDtoOut> userItems = itemService.getItems(user.getId());
        assertThat(userItems, notNullValue());
        assertThat(userItems.getFirst(), equalTo(item));
    }

    private ItemDtoIn makeItemDto(String name, String decription, boolean avaliable) {
        ItemDtoIn dto = new ItemDtoIn();
        dto.setName(name);
        dto.setDescription(decription);
        dto.setAvailable(avaliable);
        return dto;
    }
    private ItemDtoOut makeItemDtoOut(Long id,Long owner,String uri,String name, String decription, boolean avaliable) {
        ItemDtoOut dtoOut = new ItemDtoOut();
        dtoOut.setId(id);
        dtoOut.setOwner(owner);
        dtoOut.setUrl(uri);
        dtoOut.setName(name);
        dtoOut.setDescription(decription);
        dtoOut.setAvailable(avaliable);
        return dtoOut;
    }
    @Test
    void testBookingsByUser() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1=userService.saveUser(userDto1);
        UserDtoIn userDto2 = makeUserDto("new@email.com", "Ваня");
        UserDtoOut userDtoOut2=userService.saveUser(userDto2);
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество",true);
        ItemDtoOut itemDtoOut=itemService.addNewItem(userDtoOut1.getId(),itemDto);
        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        BookingDtoIn bookingDto = makeBookingDto(start, end,itemDtoOut.getId());
        BookingDtoOut bookingDtoOut=bookingService.addNewBooking(userDtoOut2.getId(),bookingDto);
        //entityManager.flush();
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(),"ALL").getFirst(), equalTo(bookingDtoOut));

    }
    private BookingDtoIn makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingDtoIn dto = new BookingDtoIn();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        return dto;
    }
    @Test
    void testItemRequest() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1=userService.saveUser(userDto1);
        ItemRequestDtoIn itemRequestDtoIn = makeItemRequestDto("Нужна хорошая вещь");
        ItemRequestDtoOut itemDtoOut=itemRequestService.addNewRequest(userDtoOut1.getId(),itemRequestDtoIn);
        // entityManager.flush();
        assertThat(itemRequestService.getAllYourselfRequests(userDtoOut1.getId()).getFirst(), equalTo(itemDtoOut));

    }
    private ItemRequestDtoIn makeItemRequestDto(String description) {
        ItemRequestDtoIn dto = new ItemRequestDtoIn();
        dto.setDescription(description);
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

}
