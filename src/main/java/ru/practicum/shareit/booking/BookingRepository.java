package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemOwner(User owner, Pageable page);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end,
                                                              Pageable page);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Pageable page);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Pageable page);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Pageable page);

    List<Booking> findAllByBooker(User booker, Pageable page);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end,
                                                           Pageable page);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Pageable page);

}
