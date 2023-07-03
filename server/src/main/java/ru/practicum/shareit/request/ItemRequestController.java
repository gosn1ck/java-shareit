package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;
    private final ItemRequestMapper itemRequestMapper;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemRequestResponse> add(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody ItemRequestDto dto) {
        log.info("New item request registration {}; user id {}", dto, userId);

        var savedItemRequest = requestService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedItemRequest.getId()).toUri();
        return ResponseEntity.created(location)
                .body(itemRequestMapper.entityToItemRequestResponse(savedItemRequest));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponse>> getAllRequestor(
            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Get item requests by requestor id: {}, state: {} ",  userId);
        var response = requestService.getAllRequestor(userId).stream()
                .map(itemRequestMapper::entityToItemRequestResponse)
                .peek(value ->
                    value.setItems(requestService.itemsByItemRequestId(value.getId()))
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemRequestResponse> getById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable("id") Long requestId) {
        log.info("get item request user id {}; request id", userId, requestId);
        var response = requestService.getById(requestId, userId);
        return response
                .map(itemRequestMapper::entityToItemRequestResponse)
                .map(value -> {
                    value.setItems(requestService.itemsByItemRequestId(value.getId()));
                    return value;
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponse>> getAll(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("get all item requests user id {}; ", userId);
        var response = requestService.getAll(userId, from, size).stream()
                .map(itemRequestMapper::entityToItemRequestResponse)
                .peek(value ->
                        value.setItems(requestService.itemsByItemRequestId(value.getId()))
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

}
