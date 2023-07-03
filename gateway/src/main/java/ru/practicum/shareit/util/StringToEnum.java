package ru.practicum.shareit.util;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.BookingState;

public class StringToEnum implements Converter<String, BookingState> {

    @Override
    public BookingState convert(String source) {
        try {
            return BookingState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return BookingState.UNSUPPORTED_STATUS;
        }
    }
}
