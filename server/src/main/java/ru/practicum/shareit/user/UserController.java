package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        log.info("Get all users");
        return ResponseEntity.ok(userMapper.entitiesToUserResponses(userService.getAll()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable("id") Long id) {
        log.info("Get user by id: {}", id);
        var response = userService.findById(id);
        return response.map(userMapper::entityToUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> add(@RequestBody UserDto userDto) {
        log.info("New user registration {}", userDto);
        var savedUser = userService.add(userDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location)
                .body(userMapper.entityToUserResponse(savedUser));
    }

    @PatchMapping(consumes = "application/json", path = "/{userId}")
    public ResponseEntity<UserResponse> update(@RequestBody UserDto userDto, @PathVariable("userId") Long id) {
        log.info("Update user request {} with id {}", userDto, id);
        var response = userService.update(userDto, id);
        return response.map(userMapper::entityToUserResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Remove user with id: {}", id);
        userService.deleteById(id);
    }

}
