package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {

    @Override
    public boolean isValid(BookingDto value, ConstraintValidatorContext context) {
        if (value.getStart() == null) {
            return true;
        }
        if (value.getEnd() == null) {
            return true;
        }

        return value.getStart().isBefore(value.getEnd());
    }

}
