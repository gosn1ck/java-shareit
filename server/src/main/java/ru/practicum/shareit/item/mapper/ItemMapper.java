package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemItemRequestResponse;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE
)
public interface ItemMapper {
    Item dtoToEntity(ItemDto dto);

    void updateEntity(@MappingTarget Item entity, ItemDto dto);

    @Mapping(source = "request.id", target = "requestId")
    ItemResponse entityToItemResponse(Item item);

    @Mapping(source = "request.id", target = "requestId")
    ItemItemRequestResponse entityToItemRequestResponse(Item item);

    List<ItemResponse> entitiesToItemResponses(List<Item> items);

}
