package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.cache.CarLocationCache;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.Location;
import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityDisableConfig.class})
public class CarControllerITest extends AbstractWebITest {

    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2022, 5, 5, 14, 35);
    private final Location location = new Location(25.323434, 20.323434, DATE_TIME);

    @Autowired
    private CarLocationCache carLocationCache;

    @Test
    @DataSet(value = "datasets/common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Add The First Car then Added Car and Ok Status Returned")
    void whenAddTheFirstCar_thenAddedCar_andOkStatusReturned() throws Exception {
        // TODO request json
        UserDto userDto = new UserDto(1,
                "Oleg Dudka",
                "test-driver@gmail.com",
                User.UserRole.DRIVER.getName(),
                true);

        CarDto carDto = new CarDto(
                0,
                "MAN",
                "AA 2455 CT",
                userDto,
                4000,
                true,
                false,
                10);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(content()
                        .json("""
                                {
                                 "name":"MAN",
                                 "licencePlate":"AA 2455 CT",
                                 "driver":{
                                    "id":1,
                                    "userName":"Oleg Dudka",
                                    "email":"test-driver@gmail.com",
                                    "role":"DRIVER",
                                    "enabled":true
                                    },
                                 "weightCapacity":4000,
                                 "cooler":true,
                                 "available":false,
                                 "travelCost": 10}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/car/dataset_update_car.yml")
    @DisplayName("when Update Car then Updated Car and Ok Status Returned")
    void whenUpdateCar_thenUpdatedCar_andOkStatusReturned() throws Exception {

        UserDto userDto = new UserDto(555,
                "Vasya Pupkin",
                "userEmail@gmail.com",
                User.UserRole.DRIVER.getName(),
                true);

        CarDto carDto = new CarDto();
        carDto.setAvailable(false);
        carDto.setName("Mazda CX5");
        carDto.setLicencePlate("AA 2244 CT");
        carDto.setDriver(userDto);
        carDto.setWeightCapacity(3500);
        carDto.setCooler(false);
        carDto.setAvailable(false);
        carDto.setTravelCost(10);

        mockMvc.perform(put("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(content()
                        .json("""
                                {
                                 "id": 1,
                                 "name":"Mazda CX5",
                                 "licencePlate":"AA 2244 CT",
                                 "driver":{
                                    "id":555,
                                    "userName":"Vasya Pupkin",
                                    "email":"userEmail@gmail.com",
                                    "role":"DRIVER",
                                    "enabled":true
                                    },
                                 "weightCapacity":3500,
                                 "cooler":false,
                                 "available":false,
                                 "travelCost": 10}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("when Delete Car With Incorrect Id then Bad Request Return")
    void whenDeleteCar_withIncorrectId_thenBadRequestReturn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/incorrectId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DataSet(value = "common/car/dataset_add_car.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/car/dataset_delete_cars.yml")
    @DisplayName("when Delete Car then Ok Status Returned")
    void whenDeleteCar_thenOkStatusReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet("common/car/dataset_cars.yml")
    @DisplayName("given Not Existing Id when Delete By Id then Exception Thrown")
    void givenNotExistingId_whenDeleteById_thenExceptionThrown() {
        assertThrows(NestedServletException.class, () ->
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/cars/5")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()));
    }

    @Test
    @DataSet(value = "common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
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
    @DataSet(value = "common/car/dataset_add_car.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
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
                                 "driver":{
                                    "id":1,
                                    "userName":"Oleg Dudka",
                                    "email":"test-driver@gmail.com",
                                    "role":"DRIVER",
                                    "enabled":true
                                    },
                                 "weightCapacity":3000,
                                 "cooler":false,
                                 "available":true,
                                 "travelCost": 10},
                                 {"id":2,
                                 "name":"MAN",
                                 "licencePlate":"AA 2455 CT",
                                 "driver":{
                                    "id":2,
                                    "userName":"Ivan Kornienko",
                                    "email":"another-test-driver@gmail.com",
                                    "role":"DRIVER",
                                    "enabled":true
                                    },
                                 "weightCapacity":4000,
                                 "cooler":true,
                                 "available":false,
                                 "travelCost": 10}]
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = "common/car/dataset_add_car.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
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
                                 "driver":{
                                    "id":2,
                                    "userName":"Ivan Kornienko",
                                    "email":"another-test-driver@gmail.com",
                                    "role":"DRIVER",
                                    "enabled":true
                                    },
                                 "weightCapacity":4000,
                                 "cooler":true,
                                 "available":false,
                                 "travelCost": 10}
                                """));
    }

    @Test
    @DisplayName("when Get Location By Id then Ok Status Returned")
    void whenGetLocation_thenOkStatusReturned() throws Exception {
        Map<Integer, Location> cacheDataMap = new ConcurrentHashMap<>();
        cacheDataMap.put(1, location);
        carLocationCache.setCacheDataMap(cacheDataMap);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/1/location")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {"longitude" :20.323434,
                                 "latitude" :25.323434,
                                 "dateTime" :"2022-05-05T14:35:00"}
                                """));
    }

    @Test
    @DisplayName("when Get Location By Id then Zero Location Coordinates And Ok Status Returned")
    void whenGetLocation_thenZeroLocationCoordinatesAndOkStatusReturned() throws Exception {
        Map<Integer, Location> cacheDataMap = new ConcurrentHashMap<>();
        cacheDataMap.put(1, location);
        carLocationCache.setCacheDataMap(cacheDataMap);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/cars/2/location")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("""
                                {"longitude" :0.0,
                                 "latitude" :0.0,
                                 "dateTime" :null}
                                """));
    }

    @Test
    @DisplayName("when Save Location then Ok Status Returned")
    public void whenSaveLocation_thenOkStatusReturned() throws Exception {
        Map<Integer, Location> cacheDataMap = new ConcurrentHashMap<>();
        carLocationCache.setCacheDataMap(cacheDataMap);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/cars/1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(location)))
                .andExpect(status().isOk());

        Map<Integer, Location> actualCacheDataMap = carLocationCache.getCacheDataMap();
        Location actualLocation = actualCacheDataMap.get(1);

        Assertions.assertEquals(1, actualCacheDataMap.size());
        Assertions.assertEquals(25.323434, actualLocation.getLatitude());
        Assertions.assertEquals(20.323434, actualLocation.getLongitude());
        Assertions.assertEquals(DATE_TIME, actualLocation.getDateTime());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/update_driver_expected.yml")
    @DisplayName("when Update Car Driver then CarDto With Updated Driver Returned")
    void whenUpdateCarDriver_thenCarDtoWithUpdatedDriverReturned() throws Exception {

        UserDto userDto = new UserDto(555,
                "Vasya Pupkin",
                "userEmail@gmail.com",
                User.UserRole.DRIVER.getName(),
                true);

        CarDto payload = new CarDto();
        payload.setName("Mercedes Sprinter");
        payload.setLicencePlate("AA 1111 CT");
        payload.setDriver(userDto);
        payload.setWeightCapacity(3000);
        payload.setCooler(false);
        payload.setAvailable(true);
        payload.setTravelCost(10);

        mockMvc.perform(put("/api/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(content()
                        .json("""
                                {
                                 "id": 1,
                                 "name":"Mercedes Sprinter",
                                 "licencePlate":"AA 1111 CT",
                                 "driver":{
                                    "id":555,
                                    "userName":"Vasya Pupkin",
                                    "email":"userEmail@gmail.com",
                                    "role":"DRIVER",
                                    "enabled":true
                                    },
                                 "weightCapacity":3000,
                                 "cooler":false,
                                 "available":true,
                                 "travelCost": 10}
                                """))
                .andExpect(status().isOk());
    }

}
