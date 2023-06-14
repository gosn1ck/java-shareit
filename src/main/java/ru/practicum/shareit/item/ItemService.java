package ru.practicum.shareit.item;

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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository,
                       BookingRepository bookingRepository,
                       ItemMapper itemMapper,
                       BookingMapper bookingMapper,
                       CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemMapper = itemMapper;
        this.bookingMapper = bookingMapper;
        this.commentMapper = commentMapper;
    }

    public List<Item> getAllByUserId(Long userId) {
        return itemRepository.findAllByOwnerIdOrderById(userId);
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
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("item with id %d not found", id));
        return Optional.of(item);
    }

    public List<Item> searchItems(String searchString) {
        if (searchString.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameOrDescription(searchString);
    }

    @Transactional
    public Comment addComment(CommentDto dto, Long itemId, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", userId));
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item with id %d not found", itemId));
        if (bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndIsBefore(user, item, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("item with id %d was not in use by user %d", itemId, userId);
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

    public ItemResponse fieldsToItemDto(ItemResponse dto) {
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

}
