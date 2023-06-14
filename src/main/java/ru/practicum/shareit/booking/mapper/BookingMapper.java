package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    Booking dtoToEntity(BookingDto dto);

    BookingResponse entityToBookingResponse(Booking entity);
    @Mapping(source = "booker.id", target = "bookerId")
    BookingShortDto entityToBookingShortDto(Booking entity);

    List<BookingResponse> entitiesToBookingResponses(List<Booking> items);

}
