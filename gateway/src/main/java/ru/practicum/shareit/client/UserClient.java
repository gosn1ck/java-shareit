package ru.practicum.shareit.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;

@FeignClient(
        value = "user",
        url = "${feign.url.user}"
)
public interface UserClient {
    @GetMapping
    ResponseEntity<List<UserResponse>> getAll();

    @GetMapping(path = "/{id}")
    ResponseEntity<UserResponse> get(@PathVariable("id") Long id);

    @PostMapping
    ResponseEntity<UserResponse> add(@RequestBody UserDto userDto);

    @PatchMapping(consumes = "application/json", path = "/{userId}")
    ResponseEntity<UserResponse> update(@RequestBody UserDto userDto, @PathVariable("userId") Long id);

    @DeleteMapping("{id}")
    void delete(@PathVariable("id") Long id);

}
