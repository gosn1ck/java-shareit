package ru.practicum.shareit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import static ru.practicum.shareit.util.Constants.USER_HEADER;

import java.util.List;

@FeignClient(
        value = "request",
        url = "${feign.url.request}"
)
public interface RequestClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<ItemRequestResponse> add(@RequestHeader(USER_HEADER) Long userId, @RequestBody ItemRequestDto dto);

    @GetMapping
    ResponseEntity<List<ItemRequestResponse>> getAllRequestor(@RequestHeader(USER_HEADER) Long userId);

    @GetMapping(path = "/{id}")
    ResponseEntity<ItemRequestResponse> getById(@RequestHeader(USER_HEADER) Long userId,
                                                @PathVariable("id") Long requestId);

    @GetMapping("/all")
    ResponseEntity<List<ItemRequestResponse>> getAll(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size);

}
