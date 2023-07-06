package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient client;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        log.info("Get all users");
        return client.getAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable("id") @Positive Long id) {
        log.info("Get user by id: {}", id);
        return client.get(id);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> add(@Valid @RequestBody UserDto userDto) {
        log.info("New user registration {}", userDto);
        return client.add(userDto);
    }

    @PatchMapping(consumes = "application/json", path = "/{userId}")
    public ResponseEntity<UserResponse> update(@RequestBody UserDto userDto,
                                               @PathVariable("userId") @Positive Long id) {
        log.info("Update user request {} with id {}", userDto, id);
        return client.update(userDto, id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Remove user with id: {}", id);
        client.delete(id);
    }
}
