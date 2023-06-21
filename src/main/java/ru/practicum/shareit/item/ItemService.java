package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    public List<Item> getAllByUserId(Long userId) {
        return itemRepository.findAllByOwnerIdOrderById(userId);
    }

    @Transactional
    public Item add(ItemDto dto, Long userId) {
        var item = itemMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresentOrElse(item::setOwner,
                () -> {
                    throw new NotFoundException("user with id %d not found", userId);
        });

        if (dto.getRequestId() != null) {
            var itemRequest = itemRequestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("item request with id %d not found", dto.getRequestId()));
            item.setRequest(itemRequest);
        }

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
        return Optional.of(getItem(id));
    }

    public List<Item> searchItems(String searchString) {
        if (searchString.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameOrDescription(searchString);
    }

    @Transactional
    public Comment addComment(CommentDto dto, Long itemId, Long userId) {
        var user = getUser(userId);
        var item = getItem(itemId);
        if (bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndIsBefore(user, item, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("item with id %d was not booked by user %d", itemId, userId);
        }

        var comment = commentMapper.dtoToEntity(dto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public List<Comment> findCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

    public ItemResponse updateBookingFields(ItemResponse dto) {
        var lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(dto.getId(), APPROVED, LocalDateTime.now());
        if (lastBooking != null) {
            dto.setLastBooking(bookingMapper.entityToBookingShortDto(lastBooking));
        }

        var nextBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStart(dto.getId(), APPROVED, LocalDateTime.now());
        if (nextBooking != null) {
            dto.setNextBooking(bookingMapper.entityToBookingShortDto(nextBooking));
        }

        return dto;
    }


    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", userId));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("item with id %d not found", id));
    }
}
