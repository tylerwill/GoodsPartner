package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.dto.CarDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import({TestSecurityEnableConfig.class})
@AutoConfigureMockMvc
@Disabled
class CarControllerSecurityITest extends AbstractWebITest {

    private CarDto carDto;

    @BeforeEach
    public void setUp() {
        carDto = new CarDto(
                0,
                "MAN",
                "AA 2455 CT",
                "Ivan Kornienko",
                4000,
                false,
                true,
                0,
                10);
    }

    @Test
    @DisplayName("when Get Cars After Auth then Ok Status Returned")
    @WithMockUser(username = "mary", roles = "ADMIN")
    void whenGetCarsAuthenticatedAndAuthorizedThenOkStatusReturned() throws Exception {

        mockMvc.perform(get("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @WithMockUser(username = "mary", roles = "ADMIN")
    @DisplayName("when Delete Car After Auth then Ok Status Returned")
    void whenDeleteCarAfterAuthThenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @WithMockUser(username = "mary", roles = "ADMIN")
    @DisplayName("when Get Car By Id After Auth  then Ok Status Returned")
    void whenGetCarByIdAfterAuthThenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Add Car After Auth then Ok Status Returned")
    @WithMockUser(username = "mary", roles = "ADMIN")
    void whenAddCarAuthenticatedAndAuthorizedThenOkStatusReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Put Car After Auth then Ok Status Returned")
    @WithMockUser(username = "mary", roles = "ADMIN")
    void whenPutCarAuthenticatedAndAuthorizedThenOkStatusReturned() throws Exception {

        mockMvc.perform(put("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("when Get Cars Without Auth then Redirected Status Returned")
    void whenGetCarNotAuthenticatedThenStatusIsRedirection() throws Exception {

        mockMvc.perform(get("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Delete Car Without Auth then Redirected Status Returned")
    void whenDeleteCarNotAuthenticatedThenStatusIsRedirection() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Get Car By Id Without Auth then Redirected Status Returned")
    void whenGetCarByIdNotAuthenticatedThenStatusIsRedirection() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Add Car Without Auth then Redirected Status Returned")
    void whenAddCarNotAuthenticatedThenStatusIsRedirection() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Put Car Without Auth then Redirected Status Returned")
    void whenPutCarNotAuthenticatedThenStatusIsRedirection() throws Exception {

        mockMvc.perform(put("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("when Get Cars Without Rights Then Forbidden Status Returned")
    @WithMockUser(username = "mary", roles = "NORIGHTS")
    void whenGetCarsWithNoAuthorizationThenStatusIsForbidden() throws Exception {

        mockMvc.perform(get("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Delete Car Without Rights Then Forbidden Status Returned")
    @WithMockUser(username = "mary", roles = "NORIGHTS")
    void whenDeleteCarWithNoAuthorizationThenStatusIsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Get Car By Id Without Rights Then Forbidden Status Returned")
    @WithMockUser(username = "mary", roles = "NORIGHTS")
    void whenGetCarByIdWithNoAuthorizationThenStatusIsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Add Car Without Rights Then Forbidden Status Returned")
    @WithMockUser(username = "mary", roles = "NORIGHTS")
    void whenAddCarWithNoAuthorizationThenStatusIsForbidden() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Put Car Without Rights Then Forbidden Status Returned")
    @WithMockUser(username = "mary", roles = "NORIGHTS")
    void whenPutCarWithNoAuthorizationThenStatusIsForbidden() throws Exception {

        mockMvc.perform(put("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isForbidden());
    }

}
