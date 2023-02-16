package com.goodspartner.web.controller;

import com.goodspartner.dto.TaskDto;
import com.goodspartner.mapper.TaskMapper;
import com.goodspartner.service.TaskService;
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
@RequestMapping(path = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping
    public List<TaskDto> getAll() {
        return taskService.findAll()
                .stream()
                .map(taskMapper::toTaskDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping("/{id}")
    public TaskDto getById(@ApiParam(value = "ID of the task to retrieve", required = true)
                           @PathVariable("id") long id) {
        return taskMapper.toTaskDto(taskService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PostMapping
    public TaskDto add(@ApiParam(value = "Task request body", type = "TaskDto", required = true)
                       @RequestBody TaskDto taskDto) {
        return taskMapper.toTaskDto(taskService.add(taskDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PutMapping("/{id}")
    public TaskDto update(@ApiParam(value = "Id of edited task", required = true)
                          @PathVariable long id,
                          @ApiParam(value = "Edited task", type = "TaskDto", required = true)
                          @RequestBody TaskDto taskDto) {
        return taskMapper.toTaskDto(taskService.update(id, taskDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @DeleteMapping("/{id}")
    public void delete(@ApiParam(value = "ID of the user to delete", required = true)
                       @PathVariable("id") long id) {
        taskService.delete(id);
    }
}
