package com.goodspartner.service.impl;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.TaskDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.Task;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.TaskNotFoundException;
import com.goodspartner.exception.TaskWithoutCarException;
import com.goodspartner.mapper.TaskMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.TaskRepository;
import com.goodspartner.service.TaskService;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.goodspartner.entity.User.UserRole.DRIVER;

@AllArgsConstructor
@Service
public class DefaultTaskService implements TaskService {

    private static final Sort DEFAULT_TASK_SORT = Sort.by(Sort.Direction.DESC, "executionDate");

    private UserService userService;
    private TaskRepository taskRepository;
    private CarRepository carRepository;
    private TaskMapper taskMapper;

    // TODO Pagination required
    @Transactional(readOnly = true)
    @Override
    public List<Task> findAll() {
        return Optional.of(userService.findByAuthentication())
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(user -> taskRepository.findByDriver(user, DEFAULT_TASK_SORT))
                .orElseGet(() -> taskRepository.findAllWithCars(DEFAULT_TASK_SORT));
    }

    @Transactional(readOnly = true)
    @Override
    public Task findById(long id) {
        return taskRepository.findByIdWithCar(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Task add(TaskDto taskDto) {
        return Optional.ofNullable(taskDto.getCar())
                .map(carDto -> carRepository.findById(carDto.getId())
                        .orElseThrow(() -> new CarNotFoundException(carDto.getId())))
                .map(car -> {
                    Task task = taskMapper.toTask(taskDto);
                    task.setCar(car);
                    return taskRepository.save(task);
                })
                .orElseThrow(TaskWithoutCarException::new);
    }

    @Transactional
    @Override
    public Task update(long id, TaskDto taskDto) {
        return taskRepository.findById(id)
                .map(task -> taskMapper.update(task, taskDto))
                .map(task -> updateCar(task, taskDto))
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private Task updateCar(Task task, TaskDto taskDto) {
        CarDto carDto = taskDto.getCar();
        if (Objects.nonNull(carDto)) {
            Car car = carRepository.findById(carDto.getId())
                    .orElseThrow(() -> new CarNotFoundException(carDto.getId()));
            task.setCar(car);
        }
        return task;
    }

    @Override
    public void delete(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }
}
