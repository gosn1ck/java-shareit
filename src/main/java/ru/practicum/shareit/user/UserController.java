package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
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
    public ResponseEntity<List<User>> getAll() {
        log.info("Get all users");
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> get(@PathVariable("id") @Positive Long id) {
        log.info("Get user by id: {}", id);
        var response = userService.findByid(id);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> add(@Valid @RequestBody UserDto userDto, Errors errors) {
        log.info("New user registration {}", userDto);
        if (errors.hasErrors()) {
            log.error("Error during new user registration: {}", errors.getAllErrors());
            return ResponseEntity.badRequest().body(userMapper.userDtoToUser(userDto));
        }
        var savedUser = userService.add(userDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(savedUser);
    }

    @PatchMapping(consumes = "application/json", path = "/{userId}")
    public ResponseEntity<User> update(@RequestBody UserDto userDto,
                                           @PathVariable("userId") @Positive Long id) {
        log.info("Update user request {} with id {}", userDto, id);
        var optUpdatedUser = userService.update(userDto, id);
        return optUpdatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        log.info("Remove user with id: {}", id);
        userService.deleteById(id);
    }

}
