package ru.practicum.shareit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

@FeignClient(
        value = "item",
        url = "${feign.url.item}"
)
public interface ItemClient {

    String USER_HEADER = "X-Sharer-User-Id";

    @GetMapping
    ResponseEntity<List<ItemResponse>> getAll(@RequestHeader(USER_HEADER) Long userId);

    @GetMapping(path = "/{id}")
    ResponseEntity<ItemResponse> get(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long id);

    @GetMapping(path = "/search")
    ResponseEntity<List<ItemResponse>> search(@RequestHeader(USER_HEADER) Long userId,
                                              @RequestParam(value = "text") String searchString);

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<ItemResponse> add(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemDto dto);

    @PatchMapping(consumes = "application/json", path = "/{id}")
    ResponseEntity<ItemResponse> update(@RequestHeader(USER_HEADER) Long userId,
                                        @PathVariable("id") Long id,
                                        @RequestBody ItemDto dto);

    @PostMapping(consumes = "application/json", path = "/{id}/comment")
    ResponseEntity<CommentResponse> addComment(@RequestHeader(USER_HEADER) Long userId,
                                               @PathVariable("id") Long id,
                                               @RequestBody CommentDto dto);

}
