package ru.yandex.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.*;
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
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ShareItServiceImplTests {
    private final EntityManager entityManager;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestServiceImpl itemRequestService;
    private final ItemRepository itemRepository;

    @Test
    void testSaveUser() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut = userService.saveUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

    }

    @Test
    void testUpdateUser() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut = userService.saveUser(userDto);
        UserDtoIn userDto2 = new UserDtoIn();
        userDto2.setName("Саша");
        UserDtoOut userDtoOut1 = userService.updateUser(userDtoOut.getId(), userDto2);
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.name = :name", User.class);
        User user = query.setParameter("name", userDto2.getName())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto2.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testDeleteUser() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut = userService.saveUser(userDto);
        ResponseEntity<String> response = userService.deleteUserById(userDtoOut.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testGetAllUser() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut = userService.saveUser(userDto);
        List<UserDtoOut> response = userService.getAllUsers();
        assertEquals(response.getFirst(), userDtoOut);
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
        UserDtoOut userDtoOut = userService.saveUser(userDto);
        assertThat(userDtoOut.getId(), notNullValue());
        assertThat(userDtoOut.getName(), equalTo(userDto.getName()));
        assertThat(userDtoOut.getEmail(), equalTo(userDto.getEmail()));
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", false);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut.getId(), itemDto);
        ItemDtoOut item = makeItemDtoOut(itemDtoOut.getId(), userDtoOut.getId(), null, "Вещь", "Хорошее качество", false);
        List<ItemDtoOut> userItems = itemService.getItems(userDtoOut.getId());
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

    private ItemDtoOut makeItemDtoOut(Long id, Long owner, String uri, String name, String decription, boolean avaliable) {
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
    void searchItemForAnyone() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");

        UserDtoOut userDtoOut = userService.saveUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", false);
        ItemDtoOut itemDtoOut = itemService.addNewItem(user.getId(), itemDto);
        ItemDtoOut item = makeItemDtoOut(itemDtoOut.getId(), userDtoOut.getId(), null, "Вещь", "Хорошее качество", false);

        ItemDtoOutDate userItem = itemService.searchItemForAnyone(item.getId());
        assertThat(userItem, notNullValue());
        assertThat(userItem.getName(), equalTo(item.getName()));
    }

    @Test
    void searchItemsText() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");

        UserDtoOut userDtoOut = userService.saveUser(userDto);

        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut.getId(), itemDto);
        ItemDtoOut item = makeItemDtoOut(itemDtoOut.getId(), userDtoOut.getId(), null, "Вещь", "Хорошее качество", false);

        List<ItemDtoOut> userItem = itemService.searchItems(userDtoOut.getId(), "Вещь");
        assertThat(userItem, notNullValue());
        assertThat(userItem.getFirst().getDescription(), equalTo(item.getDescription()));
    }

    @Test
    void updateItemTest() {
        UserDtoIn userDto = makeUserDto("some@email.com", "Пётр");

        UserDtoOut userDtoOut = userService.saveUser(userDto);

        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut.getId(), itemDto);
        ItemDtoIn itemDto2 = new ItemDtoIn();
        itemDto2.setName("Вешь2");
        ItemDtoOut updatedItem = itemService.updateItem(userDtoOut.getId(), itemDtoOut.getId(), itemDto2);
        assertThat(updatedItem, notNullValue());
        assertThat(updatedItem.getName(), equalTo(itemDto2.getName()));
    }

    @Test
    void testBookingsByUserAll() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1 = userService.saveUser(userDto1);
        UserDtoIn userDto2 = makeUserDto("new@email.com", "Ваня");
        UserDtoOut userDtoOut2 = userService.saveUser(userDto2);
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut1.getId(), itemDto);
        String str1 = "2025-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        BookingDtoIn bookingDto = makeBookingDto(start, end, itemDtoOut.getId());
        BookingDtoOut bookingDtoOut = bookingService.addNewBooking(userDtoOut2.getId(), bookingDto);
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(), "ALL").getFirst(), equalTo(bookingDtoOut));
        assertThat(bookingService.findBookingItemsByUser(userDtoOut2.getId(), "ALL"), empty());
    }

    @Test
    void testBookingsByUserCurrent() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1 = userService.saveUser(userDto1);
        UserDtoIn userDto2 = makeUserDto("new@email.com", "Ваня");
        UserDtoOut userDtoOut2 = userService.saveUser(userDto2);
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut1.getId(), itemDto);
        String str1 = "2020-04-08 12:30";
        String str2 = "2026-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        BookingDtoIn bookingDto = makeBookingDto(start, end, itemDtoOut.getId());
        BookingDtoOut bookingDtoOut = bookingService.addNewBooking(userDtoOut2.getId(), bookingDto);
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(), "CURRENT").getFirst(), equalTo(bookingDtoOut));
        assertThat(bookingService.findBookingItemsByUser(userDtoOut2.getId(), "CURRENT"), empty());
    }

    @Test
    void testBookingsByUserPast() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1 = userService.saveUser(userDto1);
        UserDtoIn userDto2 = makeUserDto("new@email.com", "Ваня");
        UserDtoOut userDtoOut2 = userService.saveUser(userDto2);
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut1.getId(), itemDto);
        String str1 = "2020-04-08 12:30";
        String str2 = "2021-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        BookingDtoIn bookingDto = makeBookingDto(start, end, itemDtoOut.getId());
        BookingDtoOut bookingDtoOut = bookingService.addNewBooking(userDtoOut2.getId(), bookingDto);
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(), "PAST").getFirst(), equalTo(bookingDtoOut));
        assertThat(bookingService.findBookingItemsByUser(userDtoOut2.getId(), "PAST"), empty());
    }

    @Test
    void testBookingsByUserFuture() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1 = userService.saveUser(userDto1);
        UserDtoIn userDto2 = makeUserDto("new@email.com", "Ваня");
        UserDtoOut userDtoOut2 = userService.saveUser(userDto2);
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut1.getId(), itemDto);
        String str1 = "2026-04-08 12:30";
        String str2 = "2027-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        BookingDtoIn bookingDto = makeBookingDto(start, end, itemDtoOut.getId());
        BookingDtoOut bookingDtoOut = bookingService.addNewBooking(userDtoOut2.getId(), bookingDto);
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(), "FUTURE").getFirst(), equalTo(bookingDtoOut));
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(), "WAITING").getFirst(), equalTo(bookingDtoOut));
        assertThat(bookingService.findBookingsByUser(userDtoOut2.getId(), "REJECTED"), empty());
        assertThat(bookingService.findBookingItemsByUser(userDtoOut2.getId(), "FUTURE"), empty());
        assertThat(bookingService.findBookingItemsByUser(userDtoOut2.getId(), "WAITING"), empty());
        assertThat(bookingService.findBookingItemsByUser(userDtoOut2.getId(), "REJECTED"), empty());
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
        UserDtoOut userDtoOut1 = userService.saveUser(userDto1);
        ItemRequestDtoIn itemRequestDtoIn = makeItemRequestDto("Нужна хорошая вещь");
        ItemRequestDtoOut itemDtoOut = itemRequestService.addNewRequest(userDtoOut1.getId(), itemRequestDtoIn);
        assertThat(itemRequestService.getAllYourselfRequests(userDtoOut1.getId()).getFirst(), equalTo(itemDtoOut));
        assertThat(itemRequestService.getAll().getFirst(), equalTo(itemDtoOut));
        assertThat(itemRequestService.getRequestById(itemDtoOut.getId()), equalTo(itemDtoOut));

    }

    private ItemRequestDtoIn makeItemRequestDto(String description) {
        ItemRequestDtoIn dto = new ItemRequestDtoIn();
        dto.setDescription(description);
        dto.setCreated(LocalDateTime.now());
        return dto;
    }

    @Test
    void testAddNewComments() {
        UserDtoIn userDto1 = makeUserDto("some@email.com", "Пётр");
        UserDtoOut userDtoOut1 = userService.saveUser(userDto1);
        UserDtoIn userDto2 = makeUserDto("new@email.com", "Ваня");
        UserDtoOut userDtoOut2 = userService.saveUser(userDto2);
        ItemDtoIn itemDto = makeItemDto("Вещь", "Хорошее качество", true);
        ItemDtoOut itemDtoOut = itemService.addNewItem(userDtoOut1.getId(), itemDto);
        String str1 = "2020-04-08 12:30";
        String str2 = "2021-04-08 12:30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(str1, formatter);
        LocalDateTime end = LocalDateTime.parse(str2, formatter);
        BookingDtoIn bookingDto = makeBookingDto(start, end, itemDtoOut.getId());
        BookingDtoOut bookingDtoOut = bookingService.addNewBooking(userDtoOut2.getId(), bookingDto);
        CommentDtoIn commentDtoIn = new CommentDtoIn();
        commentDtoIn.setText("Помогло");
        CommentDtoOut commentDtoOut = itemService.addNewComments(userDtoOut2.getId(), itemDtoOut.getId(), commentDtoIn);
        List<CommentDtoOut> commentByItem = itemService.getCommentsByItemId(itemDtoOut.getId());
        List<CommentDtoOut> allComment = itemService.getComments(userDtoOut1.getId());
        assertEquals(commentDtoOut.getText(), "Помогло");
        assertEquals(commentByItem.getFirst().getItem().getId(), itemDtoOut.getId());
        assertEquals(allComment.getFirst().getItem().getId(), itemDtoOut.getId());
    }
}
