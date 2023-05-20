package ru.practicum.shareit.item.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ItemMapperImpl.class})
class ItemMapperTest {

    private static final String NAME = "Screwdriver";
    private static final String DESCRIPTION = "Designed for a versatile performance, can be used across all professional electrical maintenance and repair jobs.";

    @Autowired
    private ItemMapper underTest;

    @DisplayName("Запрос вещи мэпится в пользователя для записи в БД")
    @Test
    void shouldMapItemDtoToItem() {
        val itemDto = new ItemDto(NAME, DESCRIPTION, TRUE);
        val item = underTest.dtoToEntity(itemDto);

        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @DisplayName("Поля вещи обновляются по запросу вещи")
    @Test
    void shouldUpdateEntity() {
        val dtoWithOnlyName = new ItemDto();
        String updatedName = "Update";
        dtoWithOnlyName.setName(updatedName);

        var item = getItem();
        String descriptionBeforeUpdate = item.getDescription();
        Boolean availableBeforeUpdate = item.getAvailable();
        underTest.updateEntity(item, dtoWithOnlyName);

        assertEquals(updatedName, item.getName());
        assertEquals(descriptionBeforeUpdate, item.getDescription());
        assertEquals(availableBeforeUpdate, item.getAvailable());

        val dtoWithOnlyDescription = new ItemDto();
        String updatedDescription = "Yet another description";
        dtoWithOnlyDescription.setDescription(updatedDescription);

        item = getItem();
        String nameBeforeUpdate = item.getName();
        availableBeforeUpdate = item.getAvailable();
        underTest.updateEntity(item, dtoWithOnlyDescription);

        assertEquals(updatedDescription, item.getDescription());
        assertEquals(nameBeforeUpdate, item.getName());
        assertEquals(availableBeforeUpdate, item.getAvailable());

        val dtoWithOnlyAvailable = new ItemDto();
        Boolean updatedAvailable = false;
        dtoWithOnlyAvailable.setAvailable(updatedAvailable);

        item = getItem();
        descriptionBeforeUpdate = item.getDescription();
        nameBeforeUpdate = item.getName();
        underTest.updateEntity(item, dtoWithOnlyAvailable);

        assertEquals(updatedAvailable, item.getAvailable());
        assertEquals(nameBeforeUpdate, item.getName());
        assertEquals(descriptionBeforeUpdate, item.getDescription());

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