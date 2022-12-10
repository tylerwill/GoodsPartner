package com.goodspartner.web.controller.route;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.GraphhopperService;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.LinkedList;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO validate and fix when FE will be ready to
@Disabled
public class RouteReorderControllerIT extends AbstractWebITest {

    private final LinkedList<RoutePoint> incorrectRoutePoints = new LinkedList<>();
    private final LinkedList<RoutePoint> routePoints = new LinkedList<>();

    @MockBean
    private GraphhopperService graphhopperService;
    @MockBean
    private ResponsePath graphhopperResponse;
    @MockBean
    private InstructionList instructions;

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets/common/recalculate_route/expected_dataset_updated_routes.yml")
    @DisplayName("when Reorder Route then Ok Status Returned")
    void whenReorderRoute_thenOkStatusReturned() throws Exception {

        Instruction instructionFirst = new Instruction(0, "", null);
        instructionFirst.setTime(11567);

        Instruction instructionSecond = new Instruction(0, "", null);
        instructionSecond.setTime(1355789);

        Instruction instructionThird = new Instruction(5, "", null);

        Instruction instructionFourth = new Instruction(0, "", null);
        instructionFourth.setTime(3556678);

        Instruction instructionFifth = new Instruction(0, "", null);
        instructionFourth.setTime(578690);

        Instruction instructionSixth = new Instruction(4, "", null);

        Translation translation = new TranslationMap.TranslationHashMap(Locale.UK);
        instructions = new InstructionList(6, translation);

        instructions.add(0, instructionFirst);
        instructions.add(1, instructionSecond);
        instructions.add(2, instructionThird);
        instructions.add(3, instructionFourth);
        instructions.add(4, instructionFifth);
        instructions.add(5, instructionSixth);

        when(graphhopperService.getRoute(anyList())).thenReturn(graphhopperResponse);
        when(graphhopperResponse.getDistance()).thenReturn(42000.0);
        when(graphhopperResponse.getTime()).thenReturn(4800000L);
        when(graphhopperResponse.getInstructions()).thenReturn(instructions);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440000/routes/1/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Not Existing DeliveryId then Not Found Return")
    void whenReorderRoute_withNotExistingDeliveryId_thenNotFoundReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440035/routes/1/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Not Existing Route Id then Not Found Return")
    void whenReorderRoute_withNotExistingRouteId_thenNotFoundReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440034/routes/10/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Incorrect DeliveryId To RouteId then Not Found Return")
    void whenReorderRoute_withIncorrectDeliveryIdToRouteId_thenNotFoundReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440000/routes/2/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route with Incorrect Route Status then Exception Thrown")
    void whenReorderRoute_withIncorrectRouteStatus_thenExceptionThrown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "123e4567-e89b-12d3-a456-556642440001/routes/2/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "common/recalculate_route/dataset_routes.yml", skipCleaningFor = "flyway_schema_history",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("when Reorder Route with Incorrect Delivery Status then Exception Thrown")
    void whenReorderRoute_withIncorrectDeliveryStatus_thenExceptionThrown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454/routes/3/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(routePoints)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "datasets/common/recalculate_route/dataset_routes.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Reorder Route Route with Incorrect RoutePointStatus then Exception Thrown")
    void whenReorderRoute_withIncorrectRoutePointStatus_thenExceptionThrown() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/deliveries/" +
                                "125e4567-e89b-12d3-a456-556642440005/routes/4/reorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectRoutePoints)))
                .andExpect(status().isBadRequest());
    }
}
