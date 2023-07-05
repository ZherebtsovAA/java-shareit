package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validation.group.UserMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated({UserMarker.OnCreate.class})
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUpdate(@PathVariable("id") @Min(0) Long userId, @Valid @RequestBody UserDto userDto) {
        return userService.patchUpdate(userId, userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") @Min(0) Long userId) {
        return userService.findById(userId);
    }

    @GetMapping
    public List<UserDto> findAll(@RequestParam(value = "numberPage", defaultValue = "0", required = false) Integer numberPage,
                                 @RequestParam(value = "numberUserToView", defaultValue = "30", required = false) Integer numberUserToView) {
        return userService.findAll(numberPage, numberUserToView);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @Min(0) Long userId) {
        userService.deleteById(userId);
    }

}