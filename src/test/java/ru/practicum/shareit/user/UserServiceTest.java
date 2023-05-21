package ru.practicum.shareit.user;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService underTest;
    @Autowired
    private UserRepository repository;

    private static final String NAME = "Ivan";
    private static final String UPDATED_NAME = "Update";
    private static final String EMAIL = "ivan@yandex.ru";
    private static final String UPDATED_EMAIL = "update@yandex.ru";

    @AfterEach
    public void cleanUpEach(){
        repository.deleteAll();
    }

    @DisplayName("Пользователь добавлен в сервис")
    @Test
    void shouldAddUser() {
        val dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);

        val user = underTest.add(dto);
        assertNotNull(user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());

        val users = underTest.getAll();
        assertTrue(users.contains(user));

        int id = users.indexOf(user);
        val savedListUser = users.get(id);
        assertEquals(user.getId(), savedListUser.getId());
        assertEquals(dto.getName(), savedListUser.getName());
        assertEquals(dto.getEmail(), savedListUser.getEmail());

        val optionalUser = underTest.findById(user.getId());
        val savedByIdUser = optionalUser.get();
        assertEquals(dto.getName(), savedByIdUser.getName());
        assertEquals(dto.getEmail(), savedByIdUser.getEmail());

    }

    @DisplayName("Пользователь не добавлеяет в сервис с такими же email")
    @Test
    void shouldNotAddUserWithSameEmail() {
        val dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);

        val user = underTest.add(dto);
        assertNotNull(user.getId());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());

        val dtoSameEmail = new UserDto();
        dtoSameEmail.setName(NAME);
        dtoSameEmail.setEmail(EMAIL);

        val exceptionUser = assertThrows(InternalServerException.class, () ->
                underTest.add(dtoSameEmail));
        assertEquals(String.format("user with email %s already exists", dtoSameEmail.getEmail()), exceptionUser.getMessage());

    }

    @DisplayName("Пользователь обновлен в сервис если он там есть")
    @Test
    void shouldUpdateUser() {
        val dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);

        val user = underTest.add(dto);

        dto.setName(UPDATED_NAME);
        dto.setEmail(UPDATED_EMAIL);

        val optUser = underTest.update(dto, user.getId());

        val updatedUser = optUser.get();
        assertEquals(UPDATED_NAME, updatedUser.getName());
        assertEquals(UPDATED_EMAIL, updatedUser.getEmail());

    }

    @DisplayName("Пользователь обновлен в сервис если он там есть")
    @Test
    void shouldNotUpdateUserWithSameEmail() {
        val dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);

        underTest.add(dto);

        val dtoNextUser = new UserDto();
        dtoNextUser.setName(UPDATED_NAME);
        dtoNextUser.setEmail(UPDATED_EMAIL);

        val userNextUser = underTest.add(dtoNextUser);

        val dtoSameEmail = new UserDto();
        dtoSameEmail.setEmail(EMAIL);

        val exceptionUser = assertThrows(InternalServerException.class, () ->
                underTest.update(dtoSameEmail, userNextUser.getId()));
        assertEquals(String.format("user with email %s already exists", dtoSameEmail.getEmail()), exceptionUser.getMessage());

    }

    @DisplayName("Пользователь удален из сервиса если он там был")
    @Test
    void shouldRemoveUser() {
        val dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);

        val user = underTest.add(dto);
        var optionalUser = underTest.findById(user.getId());
        val savedByIdUser = optionalUser.get();
        assertEquals(dto.getName(), savedByIdUser.getName());
        assertEquals(dto.getEmail(), savedByIdUser.getEmail());

        underTest.deleteById(user.getId());

        val exceptionUser = assertThrows(NotFoundException.class, () ->
                underTest.findById(user.getId()));
        assertEquals(String.format("user with id %s not found", user.getId()), exceptionUser.getMessage());

    }

}
