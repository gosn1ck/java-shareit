package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest({ItemController.class, ItemMapperImpl.class})
class ItemControllerTest {

//    @Autowired
//    private MockMvc mvc;
//    @MockBean
//    private ItemService itemService;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private ItemMapper itemMapper;

    private static final String NAME = "Screwdriver";
    private static final String UPDATED_NAME = "Screwdriver accumulated";
    private static final String DESCRIPTION = "Designed for a versatile performance, can be used across all professional electrical maintenance and repair jobs.";
    private static final String UPDATED_DESCRIPTION = "Yet another description";
    private static final String END_POINT_PATH = "/items";
    private static final String END_POINT_PATH_WITH_ID = END_POINT_PATH + "/{id}";
    private static final String END_POINT_PATH_SEARCH = END_POINT_PATH + "/search";
    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";
    private static final String TEXT_PARAM = "text";

    @Test
    @DisplayName("Ручка создания по валидному запросу вещи возвращает 201 и json c id новой вещью")
    void shouldCreateItem() throws Exception {
//        ItemDto dto = getDto();
//        Item item = getItem();
//
//        given(itemService.add(dto, item.getOwner().getId())).willReturn(item);
//
//        mvc.perform(post(END_POINT_PATH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(item.getId()))
//                .andExpect(jsonPath("$.name").value(item.getName()))
//                .andExpect(jsonPath("$.available").value(item.getAvailable()))
//                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    @DisplayName("Ручка создания по не валидному запросу вещи возвращает 400")
    void shouldNotCreateItemWithBadContent() throws Exception {
//        ItemDto dto = getDto();
//        dto.setName(null);
//        Item item = getItem();
//
//        mvc.perform(post(END_POINT_PATH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//
//        dto.setName(NAME);
//        dto.setDescription(null);
//
//        mvc.perform(post(END_POINT_PATH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//
//        dto.setDescription(DESCRIPTION);
//        dto.setAvailable(null);
//
//        mvc.perform(post(END_POINT_PATH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ручка создания по валидному запросу вещи, но без user id возвращает 400")
    void shouldNotCreateItemWithoutUserId() throws Exception {
//        ItemDto dto = getDto();
//
//        mvc.perform(post(END_POINT_PATH)
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Ручка обновления имени вещи по запросу возвращает 200 и json c id обновленной вещи")
    void shouldUpdateItemName() throws Exception {
//        ItemDto dto = getDto();
//        Item item = getItem();
//
//        var dtoOnlyName = new ItemDto();
//        dtoOnlyName.setName(UPDATED_NAME);
//        var updatedItem = new Item();
//        updatedItem.setId(item.getId());
//        updatedItem.setName(UPDATED_NAME);
//        updatedItem.setDescription(item.getDescription());
//        updatedItem.setOwner(item.getOwner());
//        updatedItem.setAvailable(item.getAvailable());
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.update(dtoOnlyName, 1L, item.getOwner().getId())).willReturn(Optional.of(updatedItem));
//
//        mvc.perform(patch(END_POINT_PATH_WITH_ID, updatedItem.getId())
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dtoOnlyName)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
//                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
//                .andExpect(jsonPath("$.available").value(updatedItem.getAvailable()))
//                .andExpect(jsonPath("$.description").value(updatedItem.getDescription()));

    }

    @Test
    @DisplayName("Ручка обновления описания вещи по запросу возвращает 200 и json c id обновленной вещи")
    void shouldUpdateItemDescription() throws Exception {
//        ItemDto dto = getDto();
//        Item item = getItem();
//
//        var dtoOnlyDescription = new ItemDto();
//        dtoOnlyDescription.setDescription(UPDATED_DESCRIPTION);
//        var updatedItem = new Item();
//        updatedItem.setId(item.getId());
//        updatedItem.setName(item.getName());
//        updatedItem.setDescription(UPDATED_DESCRIPTION);
//        updatedItem.setOwner(item.getOwner());
//        updatedItem.setAvailable(item.getAvailable());
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.update(dtoOnlyDescription, 1L, item.getOwner().getId())).willReturn(Optional.of(updatedItem));
//
//        mvc.perform(patch(END_POINT_PATH_WITH_ID, updatedItem.getId())
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dtoOnlyDescription)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
//                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
//                .andExpect(jsonPath("$.available").value(updatedItem.getAvailable()))
//                .andExpect(jsonPath("$.description").value(updatedItem.getDescription()));

    }

    @Test
    @DisplayName("Ручка обновления доступности вещи по запросу возвращает 200 и json c id обновленной вещи")
    void shouldUpdateItemAvailable() throws Exception {
//        ItemDto dto = getDto();
//        Item item = getItem();
//
//        var dtoOnlyAvailable = new ItemDto();
//        dtoOnlyAvailable.setAvailable(!item.getAvailable());
//        var updatedItem = new Item();
//        updatedItem.setId(item.getId());
//        updatedItem.setName(item.getName());
//        updatedItem.setDescription(item.getDescription());
//        updatedItem.setOwner(item.getOwner());
//        updatedItem.setAvailable(!item.getAvailable());
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.update(dtoOnlyAvailable, 1L, item.getOwner().getId())).willReturn(Optional.of(updatedItem));
//
//        mvc.perform(patch(END_POINT_PATH_WITH_ID, updatedItem.getId())
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dtoOnlyAvailable)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(updatedItem.getId()))
//                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
//                .andExpect(jsonPath("$.available").value(updatedItem.getAvailable()))
//                .andExpect(jsonPath("$.description").value(updatedItem.getDescription()));

    }

    @Test
    @DisplayName("Ручка обновления по валидному запросу вещи, но без user id возвращает 400")
    void shouldNotUpdateItemWithoutUserId() throws Exception {
//        ItemDto dto = getDto();
//        Item item = getItem();
//
//        mvc.perform(patch(END_POINT_PATH_WITH_ID, item.getId())
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Ручка получения вещи возвращает 200 и json вещи")
    void shouldGetItemById() throws Exception {
        // todo
//        ItemDto dto = getDto();
//        Item item = getItem();
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.findById(item.getId())).willReturn(Optional.of(item));
//
//        mvc.perform(get(END_POINT_PATH_WITH_ID, item.getId())
//                .header(SHARER_USER_HEADER, item.getOwner().getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(item.getId()))
//                .andExpect(jsonPath("$.name").value(item.getName()))
//                .andExpect(jsonPath("$.available").value(item.getAvailable()))
//                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    @DisplayName("Ручка получения всех вещей пользователя возвращает 200 и список json c вещами")
    void shouldGetAllItems() throws Exception {
        // todo add page
//        ItemDto dto = getDto();
//        Item item = getItem();
//        var allItems = List.of(item);
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.getAllByUserId(item.getOwner().getId())).willReturn(allItems);
//
//        mvc.perform(get(END_POINT_PATH)
//                .header(SHARER_USER_HEADER, item.getOwner().getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id").value(item.getId()))
//                .andExpect(jsonPath("$[0].name").value(item.getName()))
//                .andExpect(jsonPath("$[0].available").value(item.getAvailable()))
//                .andExpect(jsonPath("$[0].description").value(item.getDescription()));
    }

    @Test
    @DisplayName("Ручка поиска вещей возвращает 200 и список json c найденными вещами")
    void shouldSearchAllItems() throws Exception {
        // todo add page
//        ItemDto dto = getDto();
//        Item item = getItem();
//        item.setAvailable(TRUE);
//        var allItems = List.of(item);
//        String empty = "";
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.searchItems(NAME)).willReturn(allItems);
//        given(itemService.searchItems(DESCRIPTION)).willReturn(allItems);
//        given(itemService.searchItems(empty)).willReturn(Collections.emptyList());
//
//        String searchByName = item.getName();
//        String searchByDescription = item.getDescription();
//
//        mvc.perform(get(END_POINT_PATH_SEARCH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .param(TEXT_PARAM, searchByName))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id").value(item.getId()))
//                .andExpect(jsonPath("$[0].name").value(item.getName()))
//                .andExpect(jsonPath("$[0].available").value(item.getAvailable()))
//                .andExpect(jsonPath("$[0].description").value(item.getDescription()));
//
//        mvc.perform(get(END_POINT_PATH_SEARCH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .param(TEXT_PARAM, searchByDescription))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id").value(item.getId()))
//                .andExpect(jsonPath("$[0].name").value(item.getName()))
//                .andExpect(jsonPath("$[0].available").value(item.getAvailable()))
//                .andExpect(jsonPath("$[0].description").value(item.getDescription()));
//
//        mvc.perform(get(END_POINT_PATH_SEARCH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .param(TEXT_PARAM, empty))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    @DisplayName("Ручка поиска вещей возвращает 200 и пустой список json, так как нет свободных вещей")
    void shouldNotSearchItems() throws Exception {
        // todo add page
//        ItemDto dto = getDto();
//        Item item = getItem();
//        item.setAvailable(FALSE);
//
//        given(itemService.add(dto, item.getId())).willReturn(item);
//        given(itemService.searchItems(NAME)).willReturn(Collections.emptyList());
//        given(itemService.searchItems(DESCRIPTION)).willReturn(Collections.emptyList());
//
//        String searchByName = item.getName();
//        String searchByDescription = item.getDescription();
//
//        mvc.perform(get(END_POINT_PATH_SEARCH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .param(TEXT_PARAM, searchByName))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(0)));
//
//        mvc.perform(get(END_POINT_PATH_SEARCH)
//                        .header(SHARER_USER_HEADER, item.getOwner().getId())
//                        .param(TEXT_PARAM, searchByDescription))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(0)));

    }

    private static ItemDto getDto() {
        ItemDto dto = new ItemDto();
        dto.setName(NAME);
        dto.setDescription(DESCRIPTION);
        dto.setAvailable(TRUE);
        return dto;
    }

    private static Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName(NAME);
        item.setDescription(DESCRIPTION);
        item.setAvailable(TRUE);
        item.setOwner(getUser());
        return item;
    }

    private static User getUser() {
        var user = new User();
        user.setEmail("ivan@yandex.ru");
        user.setName("Ivan");
        user.setId(1L);
        return user;
    }

}