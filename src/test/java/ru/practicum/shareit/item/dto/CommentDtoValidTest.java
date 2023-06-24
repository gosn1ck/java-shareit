package ru.practicum.shareit.item.dto;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class CommentDtoValidTest {

    @Autowired
    private Validator underTest;

    @DisplayName("Запрос создания комментария проходит проверку валидации")
    @Test
    void shouldCheckValidCommentDto() {
        val dto = new CommentDto();
        dto.setText("Add comment from");

        Set<ConstraintViolation<CommentDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("Запрос создания комментария не проходит проверку валидации")
    @Test
    void shouldCheckInvalidCommentDto() {
        val dto = new CommentDto();
        dto.setText("");

        Set<ConstraintViolation<CommentDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Text should not be empty");
    }

}