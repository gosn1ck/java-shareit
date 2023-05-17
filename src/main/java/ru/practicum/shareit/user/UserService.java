package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(@Qualifier("InMemory") UserRepository userRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User add(UserDto dto) {
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(user -> {
                    throw new InternalServerException("user with email %s already exists", dto.getEmail());
                });
        var user = userMapper.userDtoToUser(dto);
        return userRepository.save(user);
    }

    public Optional<User> update(UserDto dto, Long id) {
        userRepository.findByEmail(dto.getEmail())
                .ifPresent(user -> {
                    if (!user.getId().equals(id)) {
                        throw new InternalServerException("user with email %s already exists", dto.getEmail());
                    }
                });
        var optUser = userRepository.findById(id);
        optUser.ifPresent(value ->
            userMapper.updateEntity(value, dto)
        );
        return userRepository.update(optUser.get());
    }

    public Optional<User> findByid(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
