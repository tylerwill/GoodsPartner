package com.goodspartner.repository;

import com.goodspartner.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    @Query("select a from Attachment a where a.task.id = :id")
    List<Attachment> findByTask(long id);

}