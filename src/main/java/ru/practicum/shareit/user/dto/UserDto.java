package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    private String name;
    private String email;
}