package ru.practicum.shareit.item.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CommentMapperImpl.class})
class CommentMapperTest {

    private static final String TEXT = "Add comment from";
    private static final String AUTHOR_NAME = "Nikolay";

    @Autowired
    private CommentMapper underTest;

    @DisplayName("Запрос комментария мэпится в комментарий для записи в БД")
    @Test
    void shouldMapCommentDtoToComment() {
        val dto = new CommentDto(TEXT, AUTHOR_NAME, LocalDateTime.now());
        val entity = underTest.dtoToEntity(dto);

        assertEquals(dto.getText(), entity.getText());
        assertEquals(dto.getCreated(), entity.getCreated());
    }

    @DisplayName("Комментарий мэпится в комментарий для ответа контроллера")
    @Test
    void shouldMapCommentToCommentResponse() {
        val entity = getComment();
        val request = underTest.entityToCommentResponse(entity);

        assertEquals(request.getId(), entity.getId());
        assertEquals(request.getText(), entity.getText());
        assertEquals(request.getCreated(), entity.getCreated());
        assertEquals(request.getAuthorName(), entity.getAuthor().getName());

        val entityList = List.of(entity);
        val responseList = underTest.entitiesToCommentResponses(entityList);
        assertEquals(entityList.size(), responseList.size());

    }

    private static Comment getComment() {
        var comment = new Comment();
        comment.setId(1L);
        comment.setText(TEXT);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(getUser());
        comment.setItem(getItem());
        return comment;
    }

    private static User getUser() {
        var user = new User();
        user.setEmail("ivan@yandex.ru");
        user.setName("Ivan");
        user.setId(1L);
        return user;
    }

    private static Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Screwdriver");
        item.setDescription("Designed for a versatile performance");
        item.setAvailable(TRUE);
        item.setOwner(getUser());
        return item;
    }

}