package ru.practicum.shareit.booking.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@SpringBootTest(classes = {BookingMapperImpl.class})
class BookingMapperTest {

    @Autowired
    private BookingMapper underTest;

    @DisplayName("Запрос бронирования мэпится в бронь для записи в БД")
    @Test
    void shouldMapBookingDtoToBooking() {
        val dto = new BookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1), 1L);
        val entity = underTest.dtoToEntity(dto);

        assertEquals(dto.getEnd(), entity.getEnd());
        assertEquals(dto.getStart(), entity.getStart());
    }

    @DisplayName("Бронирование мэпится в бронирование для ответа контроллера")
    @Test
    void shouldMapBookingToBookingResponse() {
        val entity = getBooking();
        val request = underTest.entityToBookingResponse(entity);

        assertEquals(request.getId(), entity.getId());
        assertEquals(request.getStart(), entity.getStart());
        assertEquals(request.getEnd(), entity.getEnd());
//        assertEquals(request.getBooker(), entity.getBooker()); this is another class
//        assertEquals(request.getItem(), entity.getItem()); this is another class
        assertEquals(request.getStatus(), entity.getStatus());

    }

    @DisplayName("Бронирование мэпится в краткое бронирование для ответа контроллера")
    @Test
    void shouldMapBookingToBookingShortDto() {
        val entity = getBooking();
        val request = underTest.entityToBookingShortDto(entity);

        assertEquals(request.getId(), entity.getId());
        assertEquals(request.getBookerId(), entity.getBooker().getId());

    }

    private static Booking getBooking() {
        var booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(getUser());
        booking.setItem(getItem());
        booking.setStatus(APPROVED);
        return booking;
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