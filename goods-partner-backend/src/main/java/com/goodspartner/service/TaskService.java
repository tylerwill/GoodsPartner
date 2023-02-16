package com.goodspartner.service;

import com.goodspartner.dto.TaskDto;
import com.goodspartner.entity.Task;

import java.util.List;

public interface TaskService {

    Task add(TaskDto carDto);

    void delete(long id);

    Task update(long id, TaskDto car);

    Task findById(long id);

    List<Task> findAll();

}
