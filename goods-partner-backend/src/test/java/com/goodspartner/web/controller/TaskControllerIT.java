package com.goodspartner.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.TaskDto;
import com.goodspartner.entity.AddressStatus;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertDeleteCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestConfigurationToCountAllQueries.class})
@AutoConfigureMockMvc
public class TaskControllerIT extends AbstractWebITest {

    private static final String TASK_API = "/api/v1/tasks";
    private static final String TASK_BY_ID_API = "/api/v1/tasks/%d";

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DataSet(value = "datasets/task/task-dataset.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("datasets/task/task-dataset.yml")
    void testTaskCrudManipulationScenario() throws Exception {
        // Given
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "photo.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "photo".getBytes()
        );
        MockMultipartFile file2
                = new MockMultipartFile(
                "file2",
                "document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "pdf".getBytes()
        );
        LocalDate executionDate = LocalDate.of(2023, 2, 14); // 14
        LocalDate updateExecutionDate = LocalDate.of(2023, 2, 15); // 15
        int taskId1 = 1;
        int taskId2 = 2;
        int car1 = 1;
        int car2 = 2;

        // Create Task
        SQLStatementCountValidator.reset();
        mockMvc.perform(
                        asLogist(
                                multipart(TASK_API)
                                        .file("files", file.getBytes())
                                        .param("task", objectMapper.writeValueAsString(getTaskDto(executionDate, "Take some coffee at 14:00. Obolon", car1)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/create-task-response.json")));
        assertSelectCount(3); // Fetch Car + nextval - tasks_id_sequence + Attachments
        assertInsertCount(2); // Insert Task + insert into attachments

        // Create Another Task
        mockMvc.perform(
                        asLogist(
                                multipart(TASK_API)
                                        .file("files", file.getBytes())
                                        .file("files", file2.getBytes())
                                        .param("task", objectMapper.writeValueAsString(getTaskDto(executionDate, "Meeting with Tolik at 18-00", car1)))))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/create-another-task-response.json")));

        // Get All by Logist
        SQLStatementCountValidator.reset();
        mockMvc.perform(
                        asLogist(
                                get(TASK_API)
                                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/get-all-tasks-by-logist.json")));
        assertSelectCount(2); // SELECT User for login + SELECT Task-Join-Car-Join-User

        // Update execution date / description / car for task
        SQLStatementCountValidator.reset();
        mockMvc.perform(
                        asLogist(
                                put(String.format(TASK_BY_ID_API, taskId2))
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .param("task", objectMapper.writeValueAsString(getTaskDto(updateExecutionDate, "Meeting with Tolik at 17-00", car2)))))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/update-task-response.json")));
        assertSelectCount(3); // SELECT existing TASK + SELECT existing Car + SELECT Attachments
        assertUpdateCount(1);

        // Get All By driver 1 - Match create Task1 response
        SQLStatementCountValidator.reset();
        mockMvc.perform(
                        asDriver(
                                get(TASK_API)
                                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/get-all-tasks-by-driver.json")));
        assertSelectCount(2); // SELECT User for user + SELECT Task-Join-Car-Join-User

        // Get all by driver 2
        mockMvc.perform(
                        asAnotherDriver(
                                get(TASK_API)
                                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/get-all-tasks-by-another-driver.json")));

        // Delete task with id=1
        SQLStatementCountValidator.reset();
        mockMvc.perform(
                        asLogist(
                                delete(String.format(TASK_BY_ID_API, taskId1))
                                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk());
        assertDeleteCount(2); // Delete Task + Delete Attachment

        // Get All by Logist - After deletion only 1 task with id=2 returned
        mockMvc.perform(
                        asLogist(
                                get(TASK_API)
                                        .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/tasks/get-all-tasks-by-logist-after-deletion.json")));
    }

    private TaskDto getTaskDto(LocalDate executionDate,
                               String description,
                               int carId) {
        CarDto carDto = CarDto.builder()
                .id(carId)
                .build();

        MapPoint mapPoint = MapPoint.builder()
                .status(AddressStatus.KNOWN)
                .address("м.Київ, Марії Лагунової, 11")
                .longitude(53.0099)
                .latitude(35.0099)
                .build();

        return TaskDto.builder()
                .car(carDto)
                .description(description)
                .executionDate(executionDate)
                .mapPoint(mapPoint)
                .build();
    }

}
