package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemMapper = itemMapper;
    }

    public List<Item> getAllByUserId(Long userId, int from, int size) {
        var page = PageRequest.of(from / size, size);
        return itemRepository.findAllByOwnerId(userId, page);
    }

    @Transactional
    public Item add(ItemDto dto, Long userId) {
        var item = itemMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresentOrElse(item::setOwner,
                () -> {throw new NotFoundException("user with id %d not found", userId);});

        return itemRepository.save(item);
    }

    @Transactional
    public Optional<Item> update(ItemDto dto, Long itemId, Long userId) {
        var optItem = itemRepository.findById(itemId);
        optItem.ifPresent(value -> {
                if (!value.getOwner().getId().equals(userId)) {
                    throw new NotFoundException("item with id %d not found from user id %d", itemId, userId);
                }
                itemMapper.updateEntity(value, dto);
                itemRepository.save(value);
        });
        return Optional.of(optItem.get());
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> searchItems(String searchString, int from, int size) {
        if (searchString.isBlank()) {
            return new ArrayList<>();
        }
        var page = PageRequest.of(from / size, size);
        return itemRepository.findByNameOrDescription(searchString, page);
    }

}
