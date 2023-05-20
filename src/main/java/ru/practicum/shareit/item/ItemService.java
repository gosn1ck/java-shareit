package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemService(@Qualifier("InMemory") ItemRepository itemRepository,
                       @Qualifier("InMemory") UserRepository userRepository,
                       ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    public List<Item> getAllByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(itemRepository::getAllByUser)
                .get();
    }

    public Item add(ItemDto dto, Long userId) {
        var item = itemMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresent(item::setOwner);
        return itemRepository.save(item);
    }

    public Optional<Item> update(ItemDto dto, Long itemId, Long userId) {
        var optItem = itemRepository.findById(itemId);
        optItem.ifPresent(value -> {
                if (!value.getOwner().getId().equals(userId)) {
                    throw new NotFoundException("item with id %d not found", itemId);
                }
                itemMapper.updateEntity(value, dto);
        });
        return itemRepository.update(optItem.get());
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> searchItems(String searchString) {
        return itemRepository.findByNameOrDescription(searchString);
    }
}
