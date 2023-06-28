package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemItemRequestResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequest add(ItemRequestDto dto, Long userId) {
        var itemRequest = itemRequestMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresentOrElse(itemRequest::setRequestor,
                () -> {
                    throw new NotFoundException("user with id %d not found", userId);
        });

        return itemRequestRepository.save(itemRequest);
    }

    @Transactional(readOnly = true)
    public List<ItemRequest> getAllRequestor(Long userId) {
        var user = getUser(userId);
        return itemRequestRepository.findAllByRequestorOrderByCreatedAsc(user);
    }

    @Transactional(readOnly = true)
    public Optional<ItemRequest> getById(Long requestId, Long userId) {
        getUser(userId);
        return Optional.of(getItemRequest(requestId));
    }

    @Transactional(readOnly = true)
    public List<ItemRequest> getAll(Long userId, Integer from, Integer size) {
        var user = getUser(userId);
        var page = PageRequest.of(from / size, size);
        return itemRequestRepository.findAllByRequestorNotOrderByCreatedAsc(user, page);
    }

    @Transactional(readOnly = true)
    public ItemRequest getItemRequest(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("item request with id %d not found", id));
    }

    @Transactional(readOnly = true)
    public List<ItemItemRequestResponse> itemsByItemRequestId(Long id) {
        return itemRepository.findAllByRequestId(id)
                .stream()
                .map(itemMapper::entityToItemRequestResponse)
                .collect(Collectors.toList());
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
    }

}
