package ru.practicum.shareit.item;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    private ItemService underTest;
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private static final String NAME = "Screwdriver";
    private static final String UPDATED_NAME = "Screwdriver accumulated";
    private static final String DESCRIPTION = "Designed for a versatile performance, can be used across all professional electrical maintenance and repair jobs.";
    private static final String UPDATED_DESCRIPTION = "Yet another description";
    private static final String USER_NAME = "Ivan";
    private static final String USER_EMAIL = "ivan@yandex.ru";

    @AfterEach
    public void cleanUpEach() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("Вещь добавлна в сервис")
    @Test
    void shouldAddItem() {
        // todo add page
//        var dto = getDto();
//        var userDto = getUserDto();
//
//        val user = userService.add(userDto);
//        val item = underTest.add(dto, user.getId());
//
//        assertNotNull(item.getId());
//        assertEquals(dto.getName(), item.getName());
//        assertEquals(dto.getDescription(), item.getDescription());
//        assertEquals(dto.getAvailable(), item.getAvailable());
//
//        val items = underTest.getAllByUserId(user.getId());
//        assertTrue(items.contains(item));
//
//        int id = items.indexOf(item);
//        val savedListItem = items.get(id);
//        assertEquals(item.getId(), savedListItem.getId());
//        assertEquals(dto.getName(), savedListItem.getName());
//        assertEquals(dto.getDescription(), savedListItem.getDescription());
//        assertEquals(dto.getAvailable(), savedListItem.getAvailable());
//
//        val optionalItem = underTest.findById(item.getId());
//        val savedByIdItem = optionalItem.get();
//        assertEquals(dto.getName(), savedByIdItem.getName());
//        assertEquals(dto.getDescription(), savedByIdItem.getDescription());
//        assertEquals(dto.getAvailable(), savedByIdItem.getAvailable());

    }

    @DisplayName("Вещь не добавляется в сервис с неизвестным пользователем")
    @Test
    void shouldNotAddItemWithUnknownUser() {
        var dto = getDto();

        val exception = assertThrows(NotFoundException.class, () ->
                underTest.add(dto, 9999L));
        assertEquals("user with id 9999 not found", exception.getMessage());

    }

    @DisplayName("Вещь обновлена в сервисе если она там есть")
    @Test
    void shouldUpdateItem() {
        var dto = getDto();
        var userDto = getUserDto();

        val user = userService.add(userDto);
        val item = underTest.add(dto, user.getId());

        dto.setName(UPDATED_NAME);
        dto.setDescription(UPDATED_DESCRIPTION);
        dto.setAvailable(FALSE);

        val optItem = underTest.update(dto, item.getId(), user.getId());

        val updatedItem = optItem.get();
        assertEquals(UPDATED_NAME, updatedItem.getName());
        assertEquals(UPDATED_DESCRIPTION, updatedItem.getDescription());
        assertEquals(FALSE, updatedItem.getAvailable());

    }

    @DisplayName("Вещь не обновлена в сервисе так как принадлежит другому пользователю")
    @Test
    void shouldNotUpdateItemWithUnknownUser() {
        var dto = getDto();
        var userDto = getUserDto();

        val user = userService.add(userDto);
        val item = underTest.add(dto, user.getId());

        dto.setName(UPDATED_NAME);
        dto.setDescription(UPDATED_DESCRIPTION);
        dto.setAvailable(FALSE);

        val exception = assertThrows(NotFoundException.class, () ->
                underTest.update(dto, item.getId(), 9999L));
        assertEquals(String.format("item with id %d not found from user id 9999", item.getId()), exception.getMessage());

    }

    @DisplayName("Найдены вещи по поиску")
    @Test
    void shouldSearchItems() {
        // todo add page
//        var dto = getDto();
//        var userDto = getUserDto();
//
//        val user = userService.add(userDto);
//        val item = underTest.add(dto, user.getId());
//
//        String searchByName = item.getName();
//        String searchByDescription = item.getDescription();
//
//        var items = underTest.searchItems(searchByName);
//        assertTrue(items.contains(item));
//
//        int id = items.indexOf(item);
//        var savedListItem = items.get(id);
//        assertEquals(item.getId(), savedListItem.getId());
//        assertEquals(dto.getName(), savedListItem.getName());
//        assertEquals(dto.getDescription(), savedListItem.getDescription());
//        assertEquals(dto.getAvailable(), savedListItem.getAvailable());
//
//        items = underTest.searchItems(searchByDescription);
//        assertTrue(items.contains(item));
//
//        id = items.indexOf(item);
//        savedListItem = items.get(id);
//        assertEquals(item.getId(), savedListItem.getId());
//        assertEquals(dto.getName(), savedListItem.getName());
//        assertEquals(dto.getDescription(), savedListItem.getDescription());
//        assertEquals(dto.getAvailable(), savedListItem.getAvailable());
//
//        dto.setAvailable(FALSE);
//        underTest.update(dto, item.getId(), user.getId());
//        items = underTest.searchItems(searchByName);
//        assertTrue(items.isEmpty());

    }


    private static ItemDto getDto() {
        val dto = new ItemDto();
        dto.setName(NAME);
        dto.setDescription(DESCRIPTION);
        dto.setAvailable(TRUE);
        return dto;
    }

    private static UserDto getUserDto() {
        val userDto = new UserDto();
        userDto.setName(USER_NAME);
        userDto.setEmail(USER_EMAIL);
        return userDto;
    }
}
