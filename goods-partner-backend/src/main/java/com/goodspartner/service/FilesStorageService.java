package com.goodspartner.service;

import com.goodspartner.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FilesStorageService {

    List<Attachment> uploadAttachments(MultipartFile[] files);

    List<Attachment> getAttachmentsByTask(long id);

    Attachment getAttachment(UUID fileId);

    void removeAttachments(List<Attachment> attachments);

    void removeAttachment(UUID id);

}
