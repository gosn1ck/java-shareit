package ru.practicum.shareit.request.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ItemRequestMapperImpl.class})
class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper underTest;
    private static final String DESCRIPTION = "Хотел бы воспользоваться щёткой для обуви";

    @DisplayName("Запрос на бронирование вещи мэпится в запрос для записи в БД")
    @Test
    void shouldMapDtoToEntity() {
        val dto = new ItemRequestDto();
        dto.setDescription(DESCRIPTION);
        val entity = underTest.dtoToEntity(dto);

        assertEquals(entity.getDescription(), entity.getDescription());
    }

    @DisplayName("запрос мэпится в запрос для ответа контроллеру")
    @Test
    void shouldMapEntityToItemRequestResponse() {
        val entity = new ItemRequest();
        entity.setId(1L);
        entity.setDescription(DESCRIPTION);
        entity.setCreated(LocalDateTime.now());
        val itemResponse = underTest.entityToItemRequestResponse(entity);
        assertEquals(itemResponse.getDescription(), entity.getDescription());
        assertEquals(itemResponse.getId(), entity.getId());
        assertEquals(itemResponse.getCreated(), entity.getCreated());
    }

}