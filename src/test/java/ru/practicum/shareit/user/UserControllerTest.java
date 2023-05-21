package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserMapper userMapper;

    private static final String NAME = "Ivan";
    private static final String UPDATED_NAME = "Update";
    private static final String EMAIL = "ivan@yandex.ru";
    private static final String UPDATED_EMAIL = "update@yandex.ru";
    private static final String END_POINT_PATH = "/users";
    private static final String END_POINT_PATH_WITH_ID = END_POINT_PATH + "/{id}";

    @Test
    @DisplayName("Ручка создания по валидному запросу пользователя возвращает 201 и json c id нового пользователя")
    void shouldCreateUser() throws Exception {
        UserDto dto = getDto();
        User user = getUser();

        given(userService.add(dto)).willReturn(user);

        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Ручка создания по не валидному запросу пользователя возвращает 400")
    void shouldNotCreateUserWithBadContent() throws Exception {
        var dto = new UserDto();
        dto.setEmail(" ");
        dto.setName(NAME);

        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        dto = new UserDto();
        dto.setName(NAME);

        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Ручка создания не добавляет пользователя с существующим email, возвращает 409")
    void shouldNotCreateUserWithSameEmail() throws Exception {
        UserDto dto = getDto();
        User user = getUser();

        given(userService.add(dto)).willReturn(user);
        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        UserDto sameEmail = getDto();

        given(userService.add(sameEmail)).willThrow(InternalServerException.class);

        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sameEmail)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Ручка обновления имени пользователя по запросу возвращает 200 и json c id обновленного пользователя")
    void shouldUpdateUserName() throws Exception {
        UserDto dto = getDto();
        User user = getUser();

        var dtoOnlyName = new UserDto();
        dtoOnlyName.setName(UPDATED_NAME);
        var updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName(UPDATED_NAME);
        updatedUser.setEmail(EMAIL);

        given(userService.add(dto)).willReturn(user);
        given(userService.update(dtoOnlyName, 1L)).willReturn(Optional.of(updatedUser));

        mvc.perform(patch(END_POINT_PATH_WITH_ID, updatedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoOnlyName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

    }

    @Test
    @DisplayName("Ручка обновления емейла пользователя по запросу возвращает 200 и json c id обновленного пользователя")
    void shouldUpdateUserEmail() throws Exception {
        UserDto dto = getDto();
        User user = getUser();

        var dtoOnlyEmail = new UserDto();
        dtoOnlyEmail.setEmail(UPDATED_EMAIL);
        var updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName(NAME);
        updatedUser.setEmail(UPDATED_EMAIL);

        given(userService.add(dto)).willReturn(user);
        given(userService.update(dtoOnlyEmail, 1L)).willReturn(Optional.of(updatedUser));

        mvc.perform(patch(END_POINT_PATH_WITH_ID, updatedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoOnlyEmail)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

    }

    @Test
    @DisplayName("Ручка получения всех пользователей возвращает 200 и список json c пользователями")
    void shouldGetAllUsers() throws Exception {
        var user = getUser();
        var allUsers = List.of(user);
        given(userService.getAll()).willReturn(allUsers);

        mvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Ручка получения пользователя возвращает 200 и json пользователя")
    void shouldGetUserById() throws Exception {
        var dto = getDto();
        var user = getUser();

        given(userService.add(dto)).willReturn(user);
        given(userService.findById(user.getId())).willReturn(Optional.of(user));

        mvc.perform(get(END_POINT_PATH_WITH_ID, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Ручка по удалению пользователя, возвращает 204")
    public void shouldRemoveUser() throws Exception {
        var dto = getDto();
        var user = getUser();

        given(userService.add(dto)).willReturn(user);
        willDoNothing().given(userService).deleteById(user.getId());

        mvc.perform(delete(END_POINT_PATH_WITH_ID, user.getId()))
                .andExpect(status().isNoContent());
    }

    private static User getUser() {
        var user = new User();
        user.setEmail(EMAIL);
        user.setName(NAME);
        user.setId(1L);
        return user;
    }

    private static UserDto getDto() {
        var dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);
        return dto;
    }

}