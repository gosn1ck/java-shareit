package ru.practicum.shareit.item;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

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
    private BookingService bookingService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

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
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @DisplayName("Вещь добавлна в сервис")
    @Test
    void shouldAddItem() {
        var dto = getDto();
        var userDto = getUserDto();

        val user = userService.add(userDto);
        val item = underTest.add(dto, user.getId());

        assertNotNull(item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());

        val items = underTest.getAllByUserId(user.getId());
        val itemFromList = items.stream().filter(value -> value.getId().equals(item.getId())).findFirst();
        assertTrue(itemFromList.isPresent());

        val savedListItem = itemFromList.get();
        assertEquals(item.getId(), savedListItem.getId());
        assertEquals(dto.getName(), savedListItem.getName());
        assertEquals(dto.getDescription(), savedListItem.getDescription());
        assertEquals(dto.getAvailable(), savedListItem.getAvailable());

        val optionalItem = underTest.findById(item.getId());
        val savedByIdItem = optionalItem.get();
        assertEquals(dto.getName(), savedByIdItem.getName());
        assertEquals(dto.getDescription(), savedByIdItem.getDescription());
        assertEquals(dto.getAvailable(), savedByIdItem.getAvailable());

    }

    @DisplayName("Вещь не добавляется в сервис с неизвестным пользователем")
    @Test
    void shouldNotAddItemWithUnknownUser() {
        var dto = getDto();

        val exception = assertThrows(NotFoundException.class, () ->
                underTest.add(dto, 9999L));
        assertEquals("user with id 9999 not found", exception.getMessage());

    }

    @DisplayName("Вещь не добавляется в сервис с неизвестным запросом")
    @Test
    void shouldNotAddItemWithRequestId() {
        var dto = getDto();
        dto.setRequestId(9999L);
        var userDto = getUserDto();

        val user = userService.add(userDto);

        val exception = assertThrows(NotFoundException.class, () ->
                underTest.add(dto, user.getId()));
        assertEquals("item request with id 9999 not found", exception.getMessage());

    }

    @DisplayName("Вещь добавляется в сервис с известным запросом")
    @Test
    void shouldAddItemWithRequestId() {
        val userDto = getUserDto();
        val user = userService.add(userDto);
        val itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("запрос");
        val request = requestService.add(itemRequestDto, user.getId());
        val dto = getDto();
        dto.setRequestId(request.getId());

        val item = underTest.add(dto, user.getId());
        assertEquals(item.getRequest().getId(), request.getId());
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
        var dto = getDto();
        var userDto = getUserDto();

        val user = userService.add(userDto);
        val item = underTest.add(dto, user.getId());

        String searchByName = item.getName();
        String searchByDescription = item.getDescription();

        var items = underTest.searchItems(searchByName);
        var itemFromList = items.stream().filter(value -> value.getId().equals(item.getId())).findFirst();
        assertTrue(itemFromList.isPresent());

        var savedListItem = itemFromList.get();
        assertEquals(item.getId(), savedListItem.getId());
        assertEquals(dto.getName(), savedListItem.getName());
        assertEquals(dto.getDescription(), savedListItem.getDescription());
        assertEquals(dto.getAvailable(), savedListItem.getAvailable());

        items = underTest.searchItems(searchByDescription);
        itemFromList = items.stream().filter(value -> value.getId().equals(item.getId())).findFirst();
        assertTrue(itemFromList.isPresent());

        dto.setAvailable(FALSE);
        underTest.update(dto, item.getId(), user.getId());
        items = underTest.searchItems(searchByName);
        assertTrue(items.isEmpty());

        items = underTest.searchItems("");
        assertTrue(items.isEmpty());

    }

    @DisplayName("Комментарий добавлен в сервис")
    @Test
    void shouldAddComment() {
        var dto = getCommentDto();
        var itemDto = getDto();
        var ownerDto = getUserDto();
        var bookerDto = new UserDto("Booker", "booker@yandex.ru");

        val owner = userService.add(ownerDto);
        val booker = userService.add(bookerDto);
        val item = underTest.add(itemDto, owner.getId());
        val bookingDto = new BookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().minusDays(1), item.getId());
        var booking = bookingService.add(bookingDto, booker.getId());
        booking = bookingService.approve(booking.getId(), owner.getId(), TRUE);
        val comment = underTest.addComment(dto, item.getId(), booker.getId());

        assertNotNull(comment.getId());
        assertEquals(dto.getText(), comment.getText());
        assertEquals(item.getId(), comment.getItem().getId());
        assertEquals(booker.getId(), comment.getAuthor().getId());

        val comments = underTest.findCommentsByItemId(item.getId());
        val commentFromList = comments.stream().filter(value -> value.getId().equals(comment.getId())).findFirst();
        assertTrue(commentFromList.isPresent());

        val savedListItem = commentFromList.get();
        assertNotNull(savedListItem.getId());
        assertEquals(dto.getText(), savedListItem.getText());

    }

    @DisplayName("Комментарий не добавлен в сервис")
    @Test
    void shouldNotAddComment() {
        var dto = getCommentDto();
        var itemDto = getDto();
        var userDto = getUserDto();

        val user = userService.add(userDto);
        val item = underTest.add(itemDto, user.getId());

        var exception = assertThrows(NotFoundException.class, () ->
                underTest.addComment(dto, item.getId(), 9999L));
        assertEquals(String.format("user with id 9999 not found", item.getId()), exception.getMessage());

        exception = assertThrows(NotFoundException.class, () ->
                underTest.addComment(dto, 9999L, user.getId()));
        assertEquals(String.format("item with id 9999 not found", item.getId()), exception.getMessage());

    }

    private static ItemDto getDto() {
        val dto = new ItemDto();
        dto.setName(NAME);
        dto.setDescription(DESCRIPTION);
        dto.setAvailable(TRUE);
        return dto;
    }

    private static CommentDto getCommentDto() {
        val dto = new CommentDto();
        dto.setText("comment for item");
        dto.setCreated(LocalDateTime.now());
        dto.setAuthorName("Nikolay");
        return dto;
    }

    private static UserDto getUserDto() {
        val userDto = new UserDto();
        userDto.setName(USER_NAME);
        userDto.setEmail(USER_EMAIL);
        return userDto;
    }

}
