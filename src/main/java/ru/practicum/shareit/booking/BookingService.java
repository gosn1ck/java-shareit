package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    public Booking add(BookingDto dto, Long userId) {
        var user = getUser(userId);
        var item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("item with id %d not found", dto.getItemId()));
        if (!item.getAvailable()) {
            throw new BadRequestException("item with id %d is not available", item.getId());
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("impossible to book item with %d ", item.getId());
        }

        var booking = bookingMapper.dtoToEntity(dto);
        booking.setStatus(WAITING);
        booking.setItem(item);
        booking.setBooker(user);

        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking approve(Long id, Long userId, Boolean approved) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("booking with id %d not found", id));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("booking with id %d not found from user id %d", id, userId);
        }

        if (booking.getStatus().equals(APPROVED)
                || booking.getStatus().equals(REJECTED)) {
            throw new BadRequestException("impossible to change booking with id %d ", id);
        }

        booking.setStatus(approved ? APPROVED : REJECTED);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Optional<Booking> getById(Long id, Long userId) {
        var optBooking = bookingRepository.findById(id);
        optBooking.ifPresentOrElse(
                value -> {
                    if (!value.getBooker().getId().equals(userId)
                            && !value.getItem().getOwner().getId().equals(userId)) {
                        throw new NotFoundException("booking with id %d not found from user id %d", id, userId);
                    }
                },
                () -> {
                    throw new NotFoundException("booking with id %d not found", id);
                });

        return Optional.of(optBooking.get());
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllOwner(Long userId, BookingState state) {
        var user = getUser(userId);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByItemOwner(user, sort));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, WAITING, sort));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings;
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBooker(Long userId, BookingState state) {
        var user = getUser(userId);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByBooker(user, sort));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, WAITING, sort));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, REJECTED, sort));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", userId));
    }

}
