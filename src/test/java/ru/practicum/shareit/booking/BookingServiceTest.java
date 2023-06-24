package ru.practicum.shareit.booking;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.BookingState.*;

@SpringBootTest
class BookingServiceTest {
    @Autowired
    private BookingService underTest;
    @Autowired
    private BookingRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    private static final String USER_NAME = "Ivan";
    private static final String USER_EMAIL = "ivan@yandex.ru";
    private static final String ITEM_NAME = "Screwdriver";
    private static final String ITEM_DESCRIPTION = "Designed for a versatile performance, can be used across all professional electrical maintenance and repair jobs.";

    @AfterEach
    public void cleanUpEach() {
        repository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @DisplayName("Не получено бронирование")
    @Test
    void shouldNotGetById() {
        var itemDto = getItemDto();
        var userDto = getUserDto();

        val owner = userService.add(userDto);
        val item = itemService.add(itemDto, owner.getId());
        userDto = getUserDto();
        userDto.setEmail("booker@yandex.ru");
        val booker = userService.add(userDto);

        val dto = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());
        var booking = underTest.add(dto, booker.getId());
        var exception = assertThrows(NotFoundException.class, () ->
                underTest.getById(booking.getId(), 9999L));
        assertEquals(
                String.format("booking with id %d not found from user id 9999", booking.getId()),
                exception.getMessage());

        exception = assertThrows(NotFoundException.class, () ->
                underTest.getById(9999L, owner.getId()));
        assertEquals("booking with id 9999 not found", exception.getMessage());

    }

    @DisplayName("Бронирование добавлено в сервис")
    @Test
    void shouldAddBooking() {
        var itemDto = getItemDto();
        var userDto = getUserDto();

        val owner = userService.add(userDto);
        val item = itemService.add(itemDto, owner.getId());
        userDto = getUserDto();
        userDto.setEmail("booker@yandex.ru");
        val booker = userService.add(userDto);

        val dto = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());

        val booking = underTest.add(dto, booker.getId());
        assertNotNull(booking.getId());
        assertEquals(booking.getStatus(), BookingStatus.WAITING);
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getBooker().getId(), booker.getId());

        val optBooking = underTest.getById(booking.getId(), booker.getId());
        assertEquals(optBooking.isPresent(), TRUE);
        val savedByIdBooking = optBooking.get();
        assertEquals(savedByIdBooking.getStatus(), BookingStatus.WAITING);
        assertEquals(savedByIdBooking.getItem().getId(), item.getId());
        assertEquals(savedByIdBooking.getBooker().getId(), booker.getId());

    }

    @DisplayName("Бронирование не добавлено в сервис")
    @Test
    void shouldANotAddBooking() {
        var itemDto = getItemDto();
        var userDto = getUserDto();

        val owner = userService.add(userDto);
        val item = itemService.add(itemDto, owner.getId());
        userDto = getUserDto();
        userDto.setEmail("booker@yandex.ru");
        val booker = userService.add(userDto);

        val dtoInvalidItem = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                9999L);

        var exceptionNotFound = assertThrows(NotFoundException.class, () ->
                underTest.add(dtoInvalidItem, booker.getId()));
        assertEquals("item with id 9999 not found", exceptionNotFound.getMessage());

        val dtoInvalidOwner = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());
        exceptionNotFound = assertThrows(NotFoundException.class, () ->
                underTest.add(dtoInvalidOwner, owner.getId()));
        assertEquals("impossible to book item with 1 ", exceptionNotFound.getMessage());

        itemDto.setAvailable(FALSE);
        itemService.update(itemDto, item.getId(), owner.getId());
        val dtoInvalidStatus = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());

        var exceptionBadRequest = assertThrows(BadRequestException.class, () ->
                underTest.add(dtoInvalidStatus, booker.getId()));
        assertEquals("item with id 1 is not available", exceptionBadRequest.getMessage());

    }

    @DisplayName("Получен список бронирований владельца")
    @Test
    void shouldGetBookingsOwner() {
        var itemDto = getItemDto();
        var userDto = getUserDto();

        val owner = userService.add(userDto);
        val item = itemService.add(itemDto, owner.getId());
        userDto = getUserDto();
        userDto.setEmail("booker@yandex.ru");
        val booker = userService.add(userDto);

        val dto = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());

        var booking = underTest.add(dto, booker.getId());
        var bookings = underTest.getAllOwner(owner.getId(), ALL, 1, 10);
        assertTrue(bookings.size() > 0);

        bookings = underTest.getAllOwner(owner.getId(), FUTURE, 1, 10);
        assertTrue(bookings.size() > 0);

        bookings = underTest.getAllOwner(owner.getId(), WAITING, 1, 10);
        assertTrue(bookings.size() > 0);

        val current = new BookingDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());
        underTest.add(current, booker.getId());

        bookings = underTest.getAllOwner(owner.getId(), CURRENT, 1, 10);
        assertTrue(bookings.size() > 0);

        val past = new BookingDto(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item.getId());
        underTest.add(past, booker.getId());
        bookings = underTest.getAllOwner(owner.getId(), PAST, 1, 10);
        assertTrue(bookings.size() > 0);

        underTest.approve(booking.getId(), owner.getId(), FALSE);
        bookings = underTest.getAllOwner(owner.getId(), REJECTED, 1, 10);
        assertTrue(bookings.size() > 0);

        var exceptionBadRequest = assertThrows(BadRequestException.class, () ->
                underTest.getAllOwner(owner.getId(), UNSUPPORTED_STATUS, 1, 10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exceptionBadRequest.getMessage());

    }

    @DisplayName("Получен список бронирований бронировавшего")
    @Test
    void shouldGetBookingsOwnerBooker() {
        var itemDto = getItemDto();
        var userDto = getUserDto();

        val owner = userService.add(userDto);
        val item = itemService.add(itemDto, owner.getId());
        userDto = getUserDto();
        userDto.setEmail("booker@yandex.ru");
        val booker = userService.add(userDto);

        val dto = new BookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());

        var booking = underTest.add(dto, booker.getId());
        var bookings = underTest.getAllBooker(booker.getId(), ALL, 1, 10);
        assertTrue(bookings.size() > 0);

        bookings = underTest.getAllBooker(booker.getId(), FUTURE, 1, 10);
        assertTrue(bookings.size() > 0);

        bookings = underTest.getAllBooker(booker.getId(), WAITING, 1, 10);
        assertTrue(bookings.size() > 0);

        val current = new BookingDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());
        underTest.add(current, booker.getId());

        bookings = underTest.getAllBooker(booker.getId(), CURRENT, 1, 10);
        assertTrue(bookings.size() > 0);

        val past = new BookingDto(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item.getId());
        underTest.add(past, booker.getId());
        bookings = underTest.getAllBooker(booker.getId(), PAST, 1, 10);
        assertTrue(bookings.size() > 0);

        underTest.approve(booking.getId(), owner.getId(), FALSE);
        bookings = underTest.getAllBooker(booker.getId(), REJECTED, 1, 10);
        assertTrue(bookings.size() > 0);

        var exceptionBadRequest = assertThrows(BadRequestException.class, () ->
                underTest.getAllBooker(booker.getId(), UNSUPPORTED_STATUS, 1, 10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exceptionBadRequest.getMessage());

    }

    private static ItemDto getItemDto() {
        val dto = new ItemDto();
        dto.setName(ITEM_NAME);
        dto.setDescription(ITEM_DESCRIPTION);
        dto.setAvailable(TRUE);
        return dto;
    }

    private static UserDto getUserDto() {
        val userDto = new UserDto();
        userDto.setName(USER_NAME);
        userDto.setEmail(USER_EMAIL);
        return userDto;
    }

}