package ru.practicum.shareit.item.model;

import lombok.*;
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
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String text;
    private LocalDateTime created;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;
    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User author;

}
