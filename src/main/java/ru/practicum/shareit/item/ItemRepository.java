package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> getAllByUser(User user);

    Optional<Item> findById(Long id);

    List<Item> findByNameOrDescription(String searchString);

    Item save(Item item);

    Optional<Item> update(Item item);

}
