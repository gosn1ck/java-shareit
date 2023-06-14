package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end,
                                                              Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Sort sort);

    List<Booking> findAllByBooker(User booker, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end,
                                                           Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerAndStatusEquals(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByBookerAndItemAndStatusEqualsAndEndIsBefore(User user, Item item, BookingStatus status,
                                                                      LocalDateTime start);

    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, BookingStatus bookingStatus, LocalDateTime date);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStart(Long itemId, BookingStatus bookingStatus, LocalDateTime now);

}
