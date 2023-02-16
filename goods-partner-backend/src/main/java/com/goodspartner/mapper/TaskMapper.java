package com.goodspartner.mapper;

import com.goodspartner.dto.TaskDto;
import com.goodspartner.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toTaskDto(Task task);

    @Mapping(target = "car", ignore = true)
    Task toTask(TaskDto taskDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "car", ignore = true)
    Task update(@MappingTarget Task task, TaskDto taskDto);
}
