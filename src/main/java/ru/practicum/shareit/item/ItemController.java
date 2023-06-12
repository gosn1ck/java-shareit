package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
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

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAll(
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items");
        return ResponseEntity.ok(itemMapper.entitiesToItemResponses(itemService.getAllByUserId(userId, from, size)));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemResponse> get(@PathVariable("id") @Positive Long id) {
        log.info("Get item by id: {}}", id);
        var response = itemService.findById(id);
        return response.map(itemMapper::entityToItemResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ItemResponse>> search(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(value = "text") String searchString) {
        log.info("Get search items by {}, by userid {}", searchString, userId);
        if (searchString.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(itemMapper.entitiesToItemResponses(itemService.searchItems(searchString, from, size)));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemResponse> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody ItemDto dto) {
        log.info("New item registration {}; user id {}", dto, userId);
        var savedItem = itemService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedItem.getId()).toUri();
        return ResponseEntity.created(location)
                .body(itemMapper.entityToItemResponse(savedItem));
    }

    @PatchMapping(consumes = "application/json", path = "/{id}")
    public ResponseEntity<ItemResponse> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("id") @Positive Long id,
                                       @RequestBody ItemDto dto) {
        log.info("Update item request {} with id {}; user id {}", dto, id, userId);
        var response = itemService.update(dto, id, userId);
        return response.map(itemMapper::entityToItemResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
