package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.FilesStorageProperties;
import com.goodspartner.entity.Attachment;
import com.goodspartner.repository.AttachmentRepository;
import com.goodspartner.service.FilesStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultFilesStorageService implements FilesStorageService {

    private final Path fileStorageLocation;
    private final AttachmentRepository attachmentRepository;

    @Autowired
    public DefaultFilesStorageService(FilesStorageProperties fileStorageProperties, AttachmentRepository attachmentRepository) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadStorage())
                .toAbsolutePath().normalize();
        this.attachmentRepository = attachmentRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @Override
    public List<Attachment> uploadAttachments(MultipartFile[] files) {
        if (files != null && files.length != 0) {
            return Arrays.stream(files)
                    .map(this::uploadFile).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Attachment> getAttachmentsByTask(long id) {
        return attachmentRepository.findByTask(id);
    }

    @Override
    public Attachment getAttachment(UUID fileId) {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

    }

    @Override
    public void removeAttachments(List<Attachment> attachments) {
        attachments.forEach(this::removeFile);
    }


    @Override
    public void removeAttachment(UUID id) {
        Optional<Attachment> attachment = attachmentRepository.findById(id);
        if (attachment.isPresent()) {
            removeFile(attachment.get());
            attachmentRepository.delete(attachment.get());
        }
    }

    private Attachment uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String uploadFileFilename = genFileName(originalFileName);
            Path savePath = fileStorageLocation.resolve(uploadFileFilename);
            Files.copy(inputStream, savePath, StandardCopyOption.REPLACE_EXISTING);

            return Attachment.builder()
                    .fileType(file.getContentType())
                    .fullPath(savePath.toString())
                    .fileNameOriginal(originalFileName)
                    .createDate(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.warn("Upload file error. {}", e.getMessage());
        }
        return null;
    }

    private void removeFile(Attachment attachment) {
        try {
            Files.delete(Path.of(attachment.getFullPath()));
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private String genFileName(String originalFileName) {
        StringJoiner uploadFileFilename = new StringJoiner("-");
        return uploadFileFilename.add(LocalDate.now().toString())
                .add(RandomStringUtils.randomAlphanumeric(8))
                .add(originalFileName).toString();
    }

}
