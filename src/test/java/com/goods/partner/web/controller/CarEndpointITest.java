package com.goods.partner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goods.partner.AbstractBaseITest;
import com.goods.partner.entity.Car;
import com.goods.partner.service.CarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc
public class CarEndpointITest extends AbstractBaseITest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    @DataSet(value = "common/dataset_cars.yml",
            disableConstraints = true)
    @DisplayName("when Add Car then Correct Json Returned")
    void whenAddCar_thenCorrectJsonReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/cars/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Mercedes Sprinter",
                                "licence_plate":"AA 1111 CT",
                                "driver":"Oleg Dudka",
                                "weight_capacity":"3000",
                                "cooler":"false",
                                "status":"enable"
                                }"""))
                .andExpect(status().isOk());

        verify(carService).createCar(any(Car.class));
    }

    @Test
    @DataSet(value = "common/dataset_cars.yml",
            disableConstraints = true)
    @DisplayName("when Delete Car then Empty Json Returned")
    void whenDeleteCar_thenEmptyJsonReturned() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(carService).removeCar(1);
    }

    @Test
    @DataSet(value = "common/dataset_cars.yml",
            disableConstraints = true)
    @DisplayName("when Update Car Status then Correct Json Returned")
    void whenUpdateCarStatus_thenCorrectJsonReturned() throws Exception {

        mockMvc.perform(put("/cars/update/1")
                        .param("status", "enable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"1",
                                "name":"Mercedes Sprinter",
                                "licence_plate":"AA 1111 CT",
                                "driver":"Oleg Dudka",
                                "weight_capacity":"2000",
                                "cooler":"false",
                                "status":"enable"
                                }"""))

                .andExpect(status().isOk());
        verify(carService).updateCarStatus((1), "enable");
    }

    @Test
    @DataSet(value = "common/dataset_cars.yml",
            disableConstraints = true)
    @DisplayName("when Delete Car With Incorrect Id then Bad Request Return")
    void whenDeleteCar_withIncorrectId_thenBadRequestReturn() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/delete/incorrectId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}