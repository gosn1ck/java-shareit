package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest({ItemRequestController.class, ItemRequestMapperImpl.class})
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private RequestService requestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemRequestMapper itemRequestMapper;

    private static final String DESCRIPTION = "Хотел бы воспользоваться щёткой для обуви";
    private static final String END_POINT_PATH = "/requests";
    private static final String END_POINT_PATH_WITH_ID = END_POINT_PATH + "/{id}";
    private static final String END_POINT_PATH_WITH_ALL = END_POINT_PATH + "/all";
    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("Ручка создания по валидному запросу возвращает 201 и json c id новым запросом")
    void shouldCreateItemRequest() throws Exception {
        var dto = getDto();
        var user = getUser();
        var itemRequest = getItemRequest();
        var response = getItemRequestResponse();
        given(requestService.add(dto, user.getId())).willReturn(itemRequest);

        mvc.perform(post(END_POINT_PATH)
                        .header(SHARER_USER_HEADER, user.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Ручка получения запроса возвращает 200 и json запроса")
    void shouldGetItemRequestById() throws Exception {
        var user = getUser();
        var itemRequest = getItemRequest();
        var response = getItemRequestResponse();

        when(requestService.getById(anyLong(), anyLong())).thenReturn(Optional.of(itemRequest));

        mvc.perform(get(END_POINT_PATH_WITH_ID, itemRequest.getId())
                        .header(SHARER_USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }

    @Test
    @DisplayName("Ручка получения списка запросов возвращает 200 и json запросов")
    void shouldGetAllItemRequest() throws Exception {
        var user = getUser();
        var itemRequest = getItemRequest();
        var response = getItemRequestResponse();

        when(requestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequest));

        mvc.perform(get(END_POINT_PATH_WITH_ALL)
                        .header(SHARER_USER_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(response.getId()))
                .andExpect(jsonPath("$[0].description").value(response.getDescription()));
    }

    @Test
    @DisplayName("Ручка получения списка запросов возвращает 200 и json запросов")
    void shouldGetRequestorItemRequest() throws Exception {
//        var user = getUser();
//        var itemRequest = getItemRequest();
//        var response = getItemRequestResponse();
//
//        when(requestService.getAllRequestor(anyLong())).thenReturn(List.of(itemRequest));
//
//        mvc.perform(get(END_POINT_PATH_WITH_ALL)
//                        .header(SHARER_USER_HEADER, user.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].id").value(response.getId()))
//                .andExpect(jsonPath("$[0].description").value(response.getDescription()));
    }

    private static ItemRequestDto getDto() {
        val dto = new ItemRequestDto();
        dto.setDescription(DESCRIPTION);
        return dto;
    }

    private static ItemRequest getItemRequest() {
        val entity = new ItemRequest();
        entity.setId(1L);
        entity.setDescription(DESCRIPTION);
        entity.setRequestor(getUser());
        return entity;
    }

    private static ItemRequestResponse getItemRequestResponse() {
        val entity = new ItemRequestResponse();
        entity.setId(1L);
        entity.setDescription(DESCRIPTION);
        entity.setCreated(LocalDateTime.now());
        return entity;
    }

    private static User getUser() {
        val user = new User();
        user.setEmail("ivan@yandex.ru");
        user.setName("Ivan");
        user.setId(1L);
        return user;
    }

}