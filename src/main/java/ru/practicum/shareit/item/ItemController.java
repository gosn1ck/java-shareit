package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping
    public ResponseEntity<List<Item>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items");
        return ResponseEntity.ok(itemService.getAllByUserId(userId));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Item> get(@PathVariable("id") @Positive Long id,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item by id: {}; user id {}", id, userId);
        var response = itemService.findById(id, userId);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Item> add(@Valid @RequestBody ItemDto dto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    Errors errors) {
        log.info("New item registration {}; user id {}", dto, userId);
        if (errors.hasErrors()) {
            log.error("Error during new item registration: {}; user id {}", errors.getAllErrors(), userId);
            return ResponseEntity.badRequest().body(itemMapper.dtoToEntity(dto));
        }
        var savedItem = itemService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedItem.getId()).toUri();
        return ResponseEntity.created(location).body(savedItem);
    }

    @PatchMapping(consumes = "application/json", path = "/{userId}")
    public ResponseEntity<Item> update(@RequestBody ItemDto dto,
                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("userId") @Positive Long itemId) {
        log.info("Update item request {} with id {}; user id {}", dto, itemId, userId);
        var optUpdatedItem = itemService.update(dto, itemId, userId);
        return optUpdatedItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
