package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User requestor;
}
