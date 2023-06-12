package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.Status.*;

@Service
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository,
                          BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    public Booking add(BookingDto dto, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", userId));
        var item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("item with id %d not found", dto.getItemId()));
        if (!item.getAvailable()) {
            throw new BadRequestException("item with id %d is not available", item.getId());
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
        booking.setStatus(approved ? APPROVED : REJECTED);
        return bookingRepository.save(booking);

    }

    public Optional<Booking> getById(Long id, Long userId) {
        var optBooking = bookingRepository.findById(id);
        optBooking.ifPresent(
                value -> {
                    if (!value.getBooker().getId().equals(userId)
                            && !value.getItem().getOwner().getId().equals(userId)) {
                        throw new NotFoundException("booking with id %d not found from user id %d", id, userId);
                    }});

        return Optional.of(optBooking.get());
    }

    public List<Booking> getAllOwner(Long userId, String state, int from, int size) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", userId));
        List<Booking> bookings = new ArrayList<>();
        var page = PageRequest.of(from / size, size);

        switch (state) {
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), page));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), page));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), page));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, WAITING, page));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, REJECTED, page));
                break;
            default: // all
                bookings.addAll(bookingRepository.findAllByItemOwner(user, page));
                break;
        }

        return bookings;

    }

    public List<Booking> getAllBooker(Long userId, String state, int from, int size) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", userId));
        List<Booking> bookings = new ArrayList<>();
        var page = PageRequest.of(from / size, size);

        switch (state) {
            case "CURRENT":
                bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), page));
                break;
            case "PAST":
                bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), page));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), page));
                break;
            case "WAITING":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, WAITING, page));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, REJECTED, page));
                break;
            default: // all
                bookings.addAll(bookingRepository.findAllByBooker(user, page));
                break;
        }

        return bookings;

    }
}
