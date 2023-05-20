package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@NoArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
//    @JsonProperty("available")
    private Boolean available;
    private User owner;
    private ItemRequest request;

}
