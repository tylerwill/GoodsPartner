package com.goodspartner.web.controller;

import com.goodspartner.dto.UserDto;
import com.goodspartner.mapper.UserMapper;
import com.goodspartner.service.UserService;
import com.goodspartner.web.controller.response.AuthResponse;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDto getById(@ApiParam(value = "ID of the user to retrieve", required = true)
                           @PathVariable("id") int id) {
        return userMapper.toUserDto(userService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public UserDto add(@ApiParam(value = "User that you want to add", type = "UserDto", required = true)
                       @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.add(userDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public UserDto update(@ApiParam(value = "Id of edited user", required = true)
                          @PathVariable int id,
                          @ApiParam(value = "Edited User", type = "UserDto", required = true)
                          @RequestBody UserDto userDto) {
        return userMapper.toUserDto(userService.update(id, userDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public UserDto delete(@ApiParam(value = "ID of the user to delete", required = true)
                          @PathVariable("id") int id) {
        return userMapper.toUserDto(userService.delete(id));
    }
}
