package com.goodspartner.service;

import com.goodspartner.dto.TaskDto;
import com.goodspartner.entity.Task;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService {

    Task add(TaskDto carDto, MultipartFile[] files);

    void delete(long id);

    Task update(long id, TaskDto car, MultipartFile[] files);

    Task findById(long id);

    List<Task> findAll();

    Task addAttachmentsToTask(long id, MultipartFile[] files);

}
