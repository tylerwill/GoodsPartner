package com.goodspartner.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.CarDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityDisableConfig.class})
public class CarControllerITest extends AbstractWebITest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Add Car then Ok Status Returned")
    void whenAddTheFirstCar_thenOkStatusReturned() throws Exception {
        CarDto carDto = CarDto.builder()
                .name("MAN")
                .licencePlate("AA 2455 CT")
                .driver("Ivan Kornienko")
                .weightCapacity(4000)
                .cooler(true)
                .available(false)
                .travelCost(10)
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_update_car.yml")
    @DisplayName("when Update Car then Ok Status Returned")
    void whenUpdateCar_thenOkStatusReturned() throws Exception {
        CarDto carDto = new CarDto();
        carDto.setAvailable(false);
        carDto.setName("Mazda CX5");
        carDto.setLicencePlate("AA 2244 CT");
        carDto.setDriver("Vasya Pupkin");
        carDto.setWeightCapacity(3500);
        carDto.setCooler(false);
        carDto.setAvailable(false);
        carDto.setLoadSize(20.5);
        carDto.setTravelCost(10);

        mockMvc.perform(put("/api/v1/cars/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("when Delete Car With Incorrect Id then Bad Request Return")
    void whenDeleteCar_withIncorrectId_thenBadRequestReturn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/incorrectId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @ExpectedDataSet("common/car/dataset_delete_cars.yml")
    @DisplayName("when Delete Car then Ok Status Returned")
    void whenDeleteCar_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_cars.yml")
    @DisplayName("given Not Existing Id when Delete By Id then Exception Thrown")
    void givenNotExistingId_whenDeleteById_thenExceptionThrown() {
        assertThrows(NestedServletException.class, () ->
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/5")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/dataset_cars.yml")
    @DisplayName("given Not Existing Id when Update Car Status By Id then Exception Thrown")
    void givenNotExistingId_whenUpdateCarStatusById_thenExceptionThrown() throws Exception {
        CarDto carDto = new CarDto();
        carDto.setAvailable(false);
        mockMvc.perform(put("/api/v1/cars/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @ExpectedDataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Get All Cars then Ok Status Returned")
    void whenGetAllCars_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                 [{
                                  "id":1,
                                 "name":"Mercedes Sprinter",
                                 "licencePlate":"AA 1111 CT",
                                 "driver":"Oleg Dudka",
                                 "weightCapacity":3000,
                                 "cooler":false,
                                 "available":true,
                                 "travelCost": 10},
                                 {"id":2,
                                 "name":"MAN",
                                 "licencePlate":"AA 2455 CT",
                                 "driver":"Ivan Kornienko",
                                 "weightCapacity":4000,
                                 "cooler":true,
                                 "available":false,
                                 "travelCost": 10}]
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet("common/car/dataset_add_car.yml")
    @ExpectedDataSet("common/car/dataset_add_car.yml")
    @DisplayName("when Get Car By Id then Ok Status Returned")
    void whenGetCarById_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {"id":2,
                                 "name":"MAN",
                                 "licencePlate":"AA 2455 CT",
                                 "driver":"Ivan Kornienko",
                                 "weightCapacity":4000,
                                 "cooler":true,
                                 "available":false,
                                 "travelCost": 10}
                                """));
    }
}