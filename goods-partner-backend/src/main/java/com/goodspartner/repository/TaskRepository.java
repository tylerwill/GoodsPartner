package com.goodspartner.repository;

import com.goodspartner.entity.Task;
import com.goodspartner.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"car", "car.driver", "attachments"})
    @Query("SELECT t FROM Task t WHERE t.car.driver = :driver")
    List<Task> findByDriver(@Param("driver") User driver, Sort sort);

    @EntityGraph(attributePaths = {"car", "car.driver", "attachments"})
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithCars(Sort sort);

    @EntityGraph(attributePaths = {"car", "car.driver", "attachments"})
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findByIdWithCar(@Param("id") long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findById(@Param("id") long id);
}
