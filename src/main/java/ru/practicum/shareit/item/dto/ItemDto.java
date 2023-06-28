package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotBlank(message = "Description should not be empty")
    private String description;
    @NotNull(message = "Available field should not be empty")
    private Boolean available;
    private Long requestId;
}
