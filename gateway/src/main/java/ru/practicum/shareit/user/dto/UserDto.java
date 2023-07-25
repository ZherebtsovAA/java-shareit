package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.validation.group.UserMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = UserMarker.OnCreate.class)
    @Pattern(regexp = "\\S+", message = "имя или логин пользователя не может содержать пробелы")
    private String name;
    @Email
    @NotBlank(groups = UserMarker.OnCreate.class)
    private String email;
}