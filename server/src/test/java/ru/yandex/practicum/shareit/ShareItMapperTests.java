package ru.yandex.practicum.shareit;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoIn;
import ru.practicum.shareit.user.dto.UserDtoOut;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShareItMapperTests {
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemMapper itemMapper;

    @Test
    void userMapperTest() {
        UserMapper userMapper = new UserMapper();
        User user = new User();
        user.setEmail("yandex@mail.com");
        user.setName("Роман");
        UserDtoOut userDtoOut = userMapper.mapUserToUserDtoOut(user);
        assertEquals(user.getName(), userDtoOut.getName());
        assertEquals(user.getEmail(), userDtoOut.getEmail());
    }

    @Test
    void userDtoMapperTest() {
        UserMapper userMapper = new UserMapper();
        UserDtoIn user = new UserDtoIn();
        user.setEmail("yandex@mail.com");
        user.setName("Роман");
        User user2 = userMapper.mapUserDtoInToUser(user);
        assertEquals(user.getName(), user2.getName());
        assertEquals(user.getEmail(), user2.getEmail());
    }

    @SneakyThrows
    @Test
    void itemDtoDateMapperTest() {
        ItemMapper itemMapper = new ItemMapper(commentRepository);
        UserDtoIn user = new UserDtoIn();
        user.setEmail("yandex@mail.com");
        user.setName("Роман");
        ItemDtoOut itemDtoOut = new ItemDtoOut(1L, user.getId(), null, "Хорошая вещь", "Вещь", true);
        when(commentRepository.findAllByItemId(any())).thenReturn(List.of(new Comment()));
        ItemDtoOutDate itemDtoOutDate = itemMapper.mapItemToItemDtoOutDate(itemDtoOut);
        assertEquals(itemDtoOut.getName(), itemDtoOutDate.getName());
        assertEquals(itemDtoOut.getDescription(), itemDtoOutDate.getDescription());
    }

    @Test
    void userDtoRequestMapperTest() {
        UserMapper userMapper = new UserMapper();
        Item item = new Item();
        item.setId(1L);
        item.setOwner(2L);
        item.setDescription("Хорошая вещь");
        item.setName("Вещь");
        item.setAvailable(true);
        ItemDtoRequest itemDtoRequest = itemMapper.mapItemToItemDtoRequest(item);
        assertEquals(item.getName(), itemDtoRequest.getName());
        assertEquals(item.getOwner(), itemDtoRequest.getOwner());
    }

    @Test
    void commentMapperTest() {
        CommentMapper commentMapper = new CommentMapper();
        CommentDtoIn commentDtoIn = new CommentDtoIn();
        commentDtoIn.setText("Комментарий");

        Comment comment = commentMapper.mapCommentDtoInToComment(commentDtoIn);
        assertEquals(comment.getText(), commentDtoIn.getText());
    }

    @Test
    void commentDtoMapperTest() {
        CommentMapper commentMapper = new CommentMapper();
        User user = new User();
        user.setEmail("yandex@mail.com");
        user.setName("Роман");
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setId(1L);
        comment.setText("Комментарий");
        CommentDtoOut commentDtoIn = commentMapper.mapCommentToCommentDtoOut(comment);
        assertEquals(comment.getText(), commentDtoIn.getText());
    }
}
