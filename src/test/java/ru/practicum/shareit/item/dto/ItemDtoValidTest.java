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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class ItemDtoValidTest {

    private static final String NAME = "Screwdriver";
    private static final String DESCRIPTION = "Designed for a versatile performance, can be used across all professional electrical maintenance and repair jobs.";

    @Autowired
    private Validator underTest;

    @DisplayName("Запрос создания вещи проходит проверку валидации")
    @Test
    void shouldCheckValidItemDto() {
        val dto = new ItemDto();
        dto.setName(NAME);
        dto.setDescription(DESCRIPTION);
        dto.setAvailable(TRUE);

        Set<ConstraintViolation<ItemDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("Запрос создания вещи не должен проходить валидацию с пустым названием")
    @Test
    void shouldNotCheckItemEmptyName() {
        val dtoEmptyName = new ItemDto();
        dtoEmptyName.setName(" ");
        dtoEmptyName.setDescription(DESCRIPTION);
        dtoEmptyName.setAvailable(FALSE);

        Set<ConstraintViolation<ItemDto>> validates = underTest.validate(dtoEmptyName);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Name should not be empty");

        val dtoNullName = new ItemDto();
        dtoNullName.setDescription(DESCRIPTION);
        dtoNullName.setAvailable(TRUE);

        validates = underTest.validate(dtoNullName);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Name should not be empty");

    }

    @DisplayName("Запрос создания вещи не должен проходить валидацию с пустым названием")
    @Test
    void shouldNotCheckItemEmptyDescription() {
        val dtoEmptyDescription = new ItemDto();
        dtoEmptyDescription.setName(NAME);
        dtoEmptyDescription.setDescription(" ");
        dtoEmptyDescription.setAvailable(FALSE);

        Set<ConstraintViolation<ItemDto>> validates = underTest.validate(dtoEmptyDescription);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Description should not be empty");

        val dtoNullDescription = new ItemDto();
        dtoNullDescription.setName(NAME);
        dtoNullDescription.setAvailable(TRUE);

        validates = underTest.validate(dtoNullDescription);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Description should not be empty");

    }

}