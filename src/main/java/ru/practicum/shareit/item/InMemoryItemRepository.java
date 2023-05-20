package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemory")
public class InMemoryItemRepository implements ItemRepository {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long currentId = 0L;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByUser(User user) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Item save(Item item) {
        item.setId(nextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> update(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
            return Optional.of(item);
        }

        throw new NotFoundException("item with id %d not found", item.getId());
    }

    @Override
    public Optional<Item> findById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        }

        throw new NotFoundException("item with id %d not found", id);
    }

    private Long nextId() {
        return ++currentId;
    }

}
