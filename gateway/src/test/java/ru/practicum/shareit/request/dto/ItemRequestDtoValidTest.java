package ru.practicum.shareit.request.dto;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class ItemRequestDtoValidTest {

    @Autowired
    private Validator underTest;
    private static final String DESCRIPTION = "Хотел бы воспользоваться щёткой для обуви";

    @DisplayName("Запрос на вещь проходит проверку валидации")
    @Test
    void shouldCheckValidBookingDto() {
        val dto = new ItemRequestDto();
        dto.setDescription(DESCRIPTION);
        Set<ConstraintViolation<ItemRequestDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("Запрос на вещь не проходит проверку валидации")
    @Test
    void shouldCheckInvalidBookingDto() {
        val dto = new ItemRequestDto();
        dto.setDescription(null);

        Set<ConstraintViolation<ItemRequestDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Description should not be empty");

        dto.setDescription(" ");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Description should not be empty");

    }

}