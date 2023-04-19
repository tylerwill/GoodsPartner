package com.goodspartner.web.controller;

import com.goodspartner.dto.AttachmentDto;
import com.goodspartner.dto.TaskDto;
import com.goodspartner.entity.Attachment;
import com.goodspartner.mapper.AttachmentMapper;
import com.goodspartner.mapper.TaskMapper;
import com.goodspartner.service.FilesStorageService;
import com.goodspartner.service.TaskService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TaskController {

    public static final String ATTACHMENT_HEADER = "attachment; filename=%s";
    public static final String ATTACHMENT_DOWNLOAD_LINK = "/api/v1/tasks/attachment/%s";

    private final TaskService taskService;
    private final FilesStorageService filesStorageService;
    private final AttachmentMapper attachmentMapper;
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
        return enrichAttachments(taskMapper.toTaskDto(taskService.findById(id)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PostMapping()
    public TaskDto add(@ApiParam(value = "Task request parameter 'task'", type = "TaskDto", required = true)
                       @RequestParam("task") TaskDto taskDto,
                       @ApiParam(value = "Parameter 'files', MultipartFile files", type = "MultipartFile[]", required = false)
                       @RequestParam(required = false, name = "files") MultipartFile[] files) {
        return enrichAttachments(taskMapper.toTaskDto(taskService.add(taskDto, files)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PutMapping("/{id}")
    public TaskDto update(@ApiParam(value = "Id of edited task", required = true)
                          @PathVariable long id,
                          @ApiParam(value = "Edited task, parameter 'task'", type = "TaskDto", required = true)
                          @RequestParam("task") TaskDto taskDto,
                          @ApiParam(value = "Parameter 'files', MultipartFile files", type = "MultipartFile[]", required = false)
                          @RequestParam(required = false, name = "files") MultipartFile[] files) {
        return enrichAttachments(taskMapper.toTaskDto(taskService.update(id, taskDto, files)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @DeleteMapping("/{id}")
    public void delete(@ApiParam(value = "ID of the user to delete", required = true)
                       @PathVariable("id") long id) {
        taskService.delete(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @PostMapping("{id}/attachment")
    public TaskDto addAttachmentToTask(@ApiParam(value = "Id of edited task", required = true)
                                       @PathVariable long id,
                                       @ApiParam(value = "Parameter 'files', files that will be added to the task", type = "MultipartFile[]", required = true)
                                       @RequestParam(required = false, name = "files") MultipartFile[] files) {
        return enrichAttachments(taskMapper.toTaskDto(taskService.addAttachmentsToTask(id, files)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN', 'DRIVER')")
    @GetMapping("attachment/{id}")
    public @ResponseBody ResponseEntity<StreamingResponseBody> getAttachment(
            @ApiParam(value = "Download file ID", required = true)
            @PathVariable UUID id) {
        Attachment attachment = filesStorageService.getAttachment(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format(ATTACHMENT_HEADER, attachment.getFileNameOriginal()))
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(outputStream -> FileCopyUtils.copy(new FileInputStream(attachment.getFullPath()), outputStream));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @DeleteMapping("attachment/{id}")
    public void deleteAttachment(@ApiParam(value = "ID of the attachment to delete", required = true)
                                 @PathVariable UUID id) {
        filesStorageService.removeAttachment(id);
    }

    private TaskDto enrichAttachments(TaskDto fullTask) {
        String uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(ATTACHMENT_DOWNLOAD_LINK)
                .toUriString();
        List<AttachmentDto> attachments = attachmentMapper
                .toAttachmetDtoList(filesStorageService.getAttachmentsByTask(fullTask.getId()));
        attachments.forEach(e -> e.setDownloadLink(String.format(uri, e.getId().toString())));
        fullTask.setAttachments(attachments);
        return fullTask;
    }
}
