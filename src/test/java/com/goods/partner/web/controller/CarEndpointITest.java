package com.goods.partner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goods.partner.AbstractBaseITest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc
public class CarEndpointITest extends AbstractBaseITest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Add Car then Ok Status Returned")
    void whenAddCar_thenOkStatusReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"3",
                                "name":"DAF",
                                "licence_plate":"AA 4567 CT",
                                "driver":"Roman Levchenko",
                                "weight_capacity":"5000",
                                "cooler":"true",
                                "status":"DISABLE"
                                }"""))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_update_car.yml")
    @DisplayName("when Update Car Status then Ok Status Returned")
    void whenUpdateCarStatus_thenOkStatusReturned() throws Exception {
        mockMvc.perform(put("/cars/update/1")
                        .param("status", "DISABLE")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Delete Car With Incorrect Id then Bad Request Return")
    void whenDeleteCar_withIncorrectId_thenBadRequestReturn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/delete/incorrectId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_delete_cars.yml")
    @DisplayName("when Delete Car then Ok Status Returned")
    void whenDeleteCar_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_cars.yml")
    @DisplayName("given Not Existing Id when Delete By Id then Exception Thrown")
    void givenNotExistingId_whenDeleteById_thenExceptionThrown() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(MockMvcRequestBuilders.delete("/cars/delete/5")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_cars.yml")
    @DisplayName("given Not Existing Id when Update Car Status By Id then Exception Thrown")
    void givenNotExistingId_whenUpdateCarStatusById_thenExceptionThrown() {
        assertThrows(Exception.class, () ->
                mockMvc.perform(put("/cars/update/5")
                                .param("status", "disable")
                                .contentType(MediaType.APPLICATION_JSON))

                        .andExpect(status().isOk()));
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @ExpectedDataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Get All Cars then Ok Status Returned")
    void whenGetAllCars_thenOkStatusReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/cars/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Get Car By Id then Ok Status Returned")
    void whenGetCarById_thenOkStatusReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/cars/get/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}