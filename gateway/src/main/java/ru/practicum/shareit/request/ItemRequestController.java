package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient client;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemRequestResponse> add(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestBody @Valid ItemRequestDto dto) {
        log.info("New item request registration {}; user id {}", dto, userId);
        return client.add(userId, dto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponse>> getAllRequestor(
            @RequestHeader(USER_HEADER) Long userId) {
        log.info("Get item requests by requestor id: {}, state: {} ",  userId);
        return client.getAllRequestor(userId);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemRequestResponse> getById(
            @RequestHeader(USER_HEADER) Long userId,
            @PathVariable("id") Long requestId) {
        log.info("get item request user id {}; request id", userId, requestId);
        return client.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponse>> getAll(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size) {
        log.info("get all item requests user id {}; ", userId);
        return client.getAll(userId, from, size);
    }

}
