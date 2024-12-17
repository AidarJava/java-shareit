package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public CommentDtoOut mapCommentToCommentDtoOut(Comment comment) {
        CommentDtoOut commentDtoOut = new CommentDtoOut();
        commentDtoOut.setId(comment.getId());
        commentDtoOut.setText(comment.getText());
        commentDtoOut.setItem(comment.getItem());
        commentDtoOut.setAuthorName(comment.getAuthor().getName());
        commentDtoOut.setCreated(comment.getCreated());
        return commentDtoOut;
    }

    public Comment mapCommentDtoInToComment(CommentDtoIn commentDtoIn) {
        Comment comment = new Comment();
        comment.setId(commentDtoIn.getId());
        comment.setText(commentDtoIn.getText());
        comment.setItem(commentDtoIn.getItem());
        comment.setAuthor(commentDtoIn.getAuthor());
        comment.setCreated(commentDtoIn.getCreated());
        return comment;
    }
}
