package ru.practicum.shareit.booking.dto;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class BookingDtoValidTest {

    @Autowired
    private Validator underTest;

    @DisplayName("Запрос создания бронирования проходит проверку валидации")
    @Test
    void shouldCheckValidBookingDto() {
        val dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<BookingDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("Запрос создания бронирования не должен проходить валидацию с пустой датой начала")
    @Test
    void shouldCheckBookingStartInvalidField() {
        val dto = new BookingDto();
        dto.setStart(null);
        dto.setEnd(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<BookingDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "start booking should not be empty");

        dto.setStart(LocalDateTime.now().minusSeconds(1));

        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "start booking must be in the future");

    }

    @DisplayName("Запрос создания бронирования не должен проходить валидацию с пустой датой окончания")
    @Test
    void shouldCheckBookingEndInvalidField() {
        val dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(null);

        Set<ConstraintViolation<BookingDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "end booking should not be empty");

    }

    @DisplayName("Запрос создания бронирования не должен проходить валидацию с если дата окончания раньше старта")
    @Test
    void shouldCheckBookingStartBeforeEndInvalidField() {
        val dto = new BookingDto();
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookingDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(
                validates.stream().findFirst().get().getMessage(), "start booking must be before end booking");

    }

}