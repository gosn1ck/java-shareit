package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User booker;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
}
