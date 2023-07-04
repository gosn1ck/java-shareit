package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemBookerResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookerResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@WebMvcTest({BookingController.class, BookingMapperImpl.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookingMapper bookingMapper;

    private static final String END_POINT_PATH = "/bookings";
    private static final String END_POINT_PATH_WITH_ID = END_POINT_PATH + "/{id}";
    private static final String END_POINT_PATH_OWNER = END_POINT_PATH + "/owner";
    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";
    private static final String APPROVED_PARAM = "approved";
    private static final String STATE_PARAM = "state";

    @Test
    @DisplayName("Ручка создания по валидному запросу возвращает 201 и json c id новым бронированием")
    void shouldCreateBooking() throws Exception {
        BookingDto dto = getDto();
        Booking booking = getBooking();
        BookingResponse response = getBookingResponse();
        User user = getUser();

        given(bookingService.add(dto, user.getId())).willReturn(booking);

        mvc.perform(post(END_POINT_PATH)
                        .header(SHARER_USER_HEADER, user.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Ручка подтверждения по валидному запросу возвращает 200 и json c id новым бронированием")
    void shouldApproveBooking() throws Exception {
        BookingDto dto = getDto();
        Booking booking = getBooking();
        BookingResponse response = getBookingResponse();
        User user = getUser();

        given(bookingService.add(dto, user.getId())).willReturn(booking);
        given(bookingService.approve(booking.getId(), user.getId(), TRUE)).willReturn(booking);
        mvc.perform(patch(END_POINT_PATH_WITH_ID, booking.getId())
                        .param(APPROVED_PARAM, "true")
                        .header(SHARER_USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Ручка получения всех бронирований собственника, возвращает 200")
    void shouldGetAllByOwner() throws Exception {
        Booking booking = getBooking();
        BookingResponse response = getBookingResponse();
        User user = getUser();

        when(bookingService.getAllOwner(anyLong(), eq(BookingState.ALL), anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get(END_POINT_PATH_OWNER)
                        .header(SHARER_USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    @DisplayName("Ручка получения всех бронирований бронировавшего, возвращает 200")
    void shouldGetAllByBooker() throws Exception {
        Booking booking = getBooking();
        BookingResponse response = getBookingResponse();

        when(bookingService.getAllBooker(anyLong(), eq(BookingState.ALL), anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        mvc.perform(get(END_POINT_PATH)
                        .header(SHARER_USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    @DisplayName("Ручка получения всех бронирований владельца, возвращает 400")
    void shouldNotGetAllByOwnerInvalidParams() throws Exception {
        mvc.perform(get(END_POINT_PATH)
                        .header(END_POINT_PATH_OWNER, 1L)
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        mvc.perform(get(END_POINT_PATH)
                        .header(END_POINT_PATH_OWNER, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mvc.perform(get(END_POINT_PATH)
                        .header(END_POINT_PATH_OWNER, 1L)
                        .param("from", "2")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ручка получения брони возвращает 200 и json брони")
    void shouldGetBookingById() throws Exception {

        Booking booking = getBooking();
        User user = getUser();
        BookingResponse response = getBookingResponse();
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        mvc.perform(get(END_POINT_PATH_WITH_ID, booking.getId())
                        .header(SHARER_USER_HEADER, user.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Ручка получения брони возвращает 400 из-за не корректного параметра State")
    void shouldNotGetAllByOwner() throws Exception {
        User user = getUser();

        when(bookingService.getAllBooker(anyLong(), eq(BookingState.UNSUPPORTED_STATUS), anyInt(), anyInt()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get(END_POINT_PATH)
                        .param(STATE_PARAM, "text")
                        .header(SHARER_USER_HEADER, user.getId()))
                .andExpect(status().isBadRequest());
    }

    private static BookingDto getDto() {
        BookingDto dto = new BookingDto();
        dto.setStart(LocalDateTime.of(2024, 06, 23, 10, 0));
        dto.setEnd(LocalDateTime.of(2024, 06, 24, 10, 0));
        dto.setItemId(1L);
        return dto;
    }

    private static Booking getBooking() {
        var booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2024, 06, 23, 10, 0));
        booking.setEnd(LocalDateTime.of(2024, 06, 24, 10, 0));
        booking.setBooker(getUser());
        booking.setItem(getItem());
        booking.setStatus(APPROVED);
        return booking;
    }

    private static BookingResponse getBookingResponse() {
        var booking = new BookingResponse();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2024, 06, 23, 10, 0));
        booking.setEnd(LocalDateTime.of(2024, 06, 24, 10, 0));
        booking.setBooker(userToUserBookerResponse(getUser()));
        booking.setItem(itemToItemBookerResponse(getItem()));
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

    private static UserBookerResponse userToUserBookerResponse(User user) {
        var response = new UserBookerResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        return response;
    }

    private static ItemBookerResponse itemToItemBookerResponse(Item item) {
        var response = new ItemBookerResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        return response;
    }

}