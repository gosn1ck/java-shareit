package ru.practicum.shareit.request;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestServiceTest {

    @Autowired
    private RequestService underTest;
    @Autowired
    private ItemRequestRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private static final String USER_NAME = "Ivan";
    private static final String USER_EMAIL = "ivan@yandex.ru";
    private static final String DESCRIPTION = "Хотел бы воспользоваться щёткой для обуви";

    @AfterEach
    public void cleanUpEach() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Запрос добавлен в сервис")
    @Test
    void shouldAddRequest() {
        var userDto = getUserDto();
        val requestor = userService.add(userDto);
        val dto = new ItemRequestDto();
        dto.setDescription(DESCRIPTION);

        val request = underTest.add(dto, requestor.getId());
        assertNotNull(request.getId());
        assertEquals(request.getRequestor().getId(), requestor.getId());
        assertEquals(request.getDescription(), dto.getDescription());

        val savedRequest = underTest.getById(request.getId(), requestor.getId());
        assertTrue(savedRequest.isPresent());

        val requests = underTest.getAllRequestor(requestor.getId());
        assertTrue(requests.size() > 0);

        userDto.setEmail("user@user.com");
        val user = userService.add(userDto);
        val all = underTest.getAll(user.getId(), 1, 10);
        assertTrue(all.size() > 0);
    }

    @DisplayName("Запрос не добавлен в сервис")
    @Test
    void shouldNotAddRequest() {
        val dto = new ItemRequestDto();
        dto.setDescription(DESCRIPTION);

        var exceptionNotFound = assertThrows(NotFoundException.class, () ->
                underTest.add(dto, 9999L));
        assertEquals("user with id 9999 not found", exceptionNotFound.getMessage());

        var userDto = getUserDto();
        val requestor = userService.add(userDto);

        exceptionNotFound = assertThrows(NotFoundException.class, () ->
                underTest.getById(9999L, requestor.getId()));
        assertEquals("item request with id 9999 not found", exceptionNotFound.getMessage());

    }

    private static UserDto getUserDto() {
        val userDto = new UserDto();
        userDto.setName(USER_NAME);
        userDto.setEmail(USER_EMAIL);
        return userDto;
    }

}