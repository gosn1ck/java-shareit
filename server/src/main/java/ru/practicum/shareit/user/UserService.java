package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.ClientErrorException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User add(UserDto dto) {
        var user = userMapper.dtoToEntity(dto);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("user with email %s already exists", dto.getEmail());
        }

    }

    @Transactional
    public Optional<User> update(UserDto dto, Long id) {
        var optUser = userRepository.findById(id);
        optUser.ifPresent(value ->
            userMapper.updateEntity(value, dto)
        );
        try {
            return Optional.of(userRepository.save(optUser.get()));
        } catch (DataIntegrityViolationException e) {
            throw new ClientErrorException("user with email %s already exists", dto.getEmail());
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

}
