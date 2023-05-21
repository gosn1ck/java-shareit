package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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

    @GetMapping
    public ResponseEntity<List<Item>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items");
        return ResponseEntity.ok(itemService.getAllByUserId(userId));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Item> get(@PathVariable("id") @Positive Long id) {
        log.info("Get item by id: {}}", id);
        var response = itemService.findById(id);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<Item>> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(value = "text") String searchString) {
        log.info("Get search items by {}, by userid {}", searchString, userId);
        if (searchString.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(itemService.searchItems(searchString));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Item> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody ItemDto dto) {
        log.info("New item registration {}; user id {}", dto, userId);
        var savedItem = itemService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedItem.getId()).toUri();
        return ResponseEntity.created(location).body(savedItem);
    }

    @PatchMapping(consumes = "application/json", path = "/{id}")
    public ResponseEntity<Item> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("id") @Positive Long id,
                                       @RequestBody ItemDto dto) {
        log.info("Update item request {} with id {}; user id {}", dto, id, userId);
        var optUpdatedItem = itemService.update(dto, id, userId);
        return optUpdatedItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
