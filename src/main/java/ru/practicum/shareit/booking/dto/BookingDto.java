package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validator.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd
public class BookingDto {
    @NotNull(message = "start booking should not be empty")
    @FutureOrPresent(message = "start booking must be in the future")
    private LocalDateTime start;
    @NotNull(message = "end booking should not be empty")
    @Future(message = "end booking must be in the future")
    private LocalDateTime end;
    private Long itemId;
}
