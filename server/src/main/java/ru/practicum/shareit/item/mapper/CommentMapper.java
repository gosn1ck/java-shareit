package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    Comment dtoToEntity(CommentDto dto);

    @Mapping(source = "author.name", target = "authorName")
    CommentResponse entityToCommentResponse(Comment comment);

    List<CommentResponse> entitiesToCommentResponses(List<Comment> comments);

}
