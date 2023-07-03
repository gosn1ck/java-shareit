package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Get all items by user id: {}", userId);
        var items = itemMapper.entitiesToItemResponses(itemService.getAllByUserId(userId));
        items.forEach(item -> {
            item.setComments(
                    commentMapper.entitiesToCommentResponses(
                            itemService.findCommentsByItemId(
                                    item.getId())));
            itemService.updateBookingFields(item);
        });
        return ResponseEntity.ok(items);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemResponse> get(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long id) {
        log.info("Get item by id: {}", id);
        var optionalItem = itemService.findById(id);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var itemResponse = itemMapper.entityToItemResponse(optionalItem.get());
        var comments = itemService.findCommentsByItemId(itemResponse.getId());
        itemResponse.setComments(commentMapper.entitiesToCommentResponses(comments));
        if (optionalItem.get().getOwner().getId().equals(userId)) {
            itemResponse = itemService.updateBookingFields(itemResponse);
        }
        return ResponseEntity.ok(itemResponse);

    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ItemResponse>> search(@RequestHeader(USER_HEADER) Long userId,
                                                     @RequestParam(value = "text") String searchString) {
        log.info("Get search items by {}, by userid {}", searchString, userId);
        if (searchString.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(itemMapper.entitiesToItemResponses(itemService.searchItems(searchString)));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemResponse> add(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto dto) {
        log.info("New item registration {}; user id {}", dto, userId);
        var savedItem = itemService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedItem.getId()).toUri();
        return ResponseEntity.created(location)
                .body(itemMapper.entityToItemResponse(savedItem));
    }

    @PatchMapping(consumes = "application/json", path = "/{id}")
    public ResponseEntity<ItemResponse> update(@RequestHeader(USER_HEADER) Long userId,
                                       @PathVariable("id") Long id,
                                       @RequestBody ItemDto dto) {
        log.info("Update item request {} with id {}; user id {}", dto, id, userId);
        var response = itemService.update(dto, id, userId);
        return response.map(itemMapper::entityToItemResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json", path = "/{id}/comment")
    public ResponseEntity<CommentResponse> addComment(@RequestHeader(USER_HEADER) Long userId,
                                      @PathVariable("id") Long id,
                                      @RequestBody CommentDto dto) {
        log.info("New comment registration {} to item id {}; user id {}", dto, id, userId);
        var response = itemService.addComment(dto, id, userId);
        return ResponseEntity.ok(commentMapper.entityToCommentResponse(response));
    }

}
