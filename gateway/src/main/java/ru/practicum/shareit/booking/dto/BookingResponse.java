package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemBookerResponse;
import ru.practicum.shareit.user.dto.UserBookerResponse;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserBookerResponse booker;
    private ItemBookerResponse item;
    private BookingStatus status;
}
