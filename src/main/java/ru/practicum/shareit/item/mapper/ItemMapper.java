package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    Item dtoToEntity(ItemDto dto);

    void updateEntity(@MappingTarget Item entity, ItemDto dto);

    ItemResponse entityToItemResponse(Item item);

    List<ItemResponse> entitiesToItemResponses(List<Item> items);

}
