package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll(@RequestHeader(USER_HEADER) Long userId) {
        log.info("Get all items by user id: {}", userId);
        return client.getAll(userId);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemResponse> get(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long id) {
        log.info("Get item by id: {}", id);
        return client.get(userId, id);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ItemResponse>> search(@RequestHeader(USER_HEADER) Long userId,
                                                     @RequestParam(value = "text") String searchString) {
        log.info("Get search items by {}, by userid {}", searchString, userId);
        return client.search(userId, searchString);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemResponse> add(@RequestHeader(USER_HEADER) Long userId,
                                            @Valid @RequestBody ItemDto dto) {
        log.info("New item registration {}; user id {}", dto, userId);
        return client.add(userId, dto);
    }

    @PatchMapping(consumes = "application/json", path = "/{id}")
    public ResponseEntity<ItemResponse> update(@RequestHeader(USER_HEADER) Long userId,
                                       @PathVariable("id") Long id,
                                       @RequestBody ItemDto dto) {
        log.info("Update item request {} with id {}; user id {}", dto, id, userId);
        return client.update(userId, id, dto);
    }

    @PostMapping(consumes = "application/json", path = "/{id}/comment")
    public ResponseEntity<CommentResponse> addComment(@RequestHeader(USER_HEADER) Long userId,
                                                      @PathVariable("id") Long id,
                                                      @Valid @RequestBody CommentDto dto) {
        log.info("New comment registration {} to item id {}; user id {}", dto, id, userId);
        return client.addComment(userId, id, dto);
    }

}
