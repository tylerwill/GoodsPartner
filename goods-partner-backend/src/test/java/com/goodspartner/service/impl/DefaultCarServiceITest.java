package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.User;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;

@Import(TestConfigurationToCountAllQueries.class)
@DBRider
class DefaultCarServiceITest extends AbstractBaseITest {

    private static final int CAR_ID = 1;

    @Autowired
    private DefaultCarService carService;

    @Test
    @DataSet(value = "/datasets/common/car/adding_new_car.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("validate Queries After Add Car")
    void validateQueriesAfterAddCar() {
        SQLStatementCountValidator.reset();

        UserDto userDto = new UserDto(1,
                "Oleg Dudka",
                "test-driver@gmail.com",
                User.UserRole.DRIVER.name(),
                true);

        CarDto carDto = new CarDto();
        carDto.setName("DAF");
        carDto.setDriver(userDto);
        carDto.setCooler(true);
        carDto.setLicencePlate("AA 4567 CT");
        carDto.setWeightCapacity(5000);
        carDto.setAvailable(false);

        carService.add(carDto);

        assertSelectCount(2); // Validated Select for car + Select for Users
    }

    @Test
    @DataSet(value = "common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("validate Queries After Delete Car")
    void validateQueriesAfterDeleteCar() {
        SQLStatementCountValidator.reset();

        carService.delete(CAR_ID);

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("validate Queries after Find Al lCars")
    void validateQueries_afterFindAllCars() {
        SQLStatementCountValidator.reset();

        carService.findAll();

        assertSelectCount(1);
    }

    @Test
    @DataSet(value = "/datasets/common/car/dataset_cars.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @DisplayName("validate Queries AfterUpdate Car Status")
    void validateQueries_afterUpdateCarStatus() {
        // Given
        Car carBefore = carService.findById(CAR_ID);
        Assertions.assertEquals("Mercedes Sprinter", carBefore.getName());
        Assertions.assertEquals("AA 1111 CT", carBefore.getLicencePlate());
        Assertions.assertEquals("Oleg Dudka", carBefore.getDriver().getUsername());
        Assertions.assertEquals(3000, carBefore.getWeightCapacity());
        Assertions.assertFalse(carBefore.isCooler());
        Assertions.assertTrue(carBefore.isAvailable());
        Assertions.assertEquals(10, carBefore.getTravelCost());

        // When
        UserDto userDto = new UserDto(555,
                "Vasya Pupkin",
                "userEmail@gmail.com",
                User.UserRole.DRIVER.name(),
                true);

        CarDto payload = new CarDto();
        payload.setAvailable(false);
        payload.setName("Mazda CX5");
        payload.setLicencePlate("AA 2222 CT");
        payload.setDriver(userDto);
        payload.setWeightCapacity(3500);
        payload.setCooler(false);
        payload.setAvailable(false);
        payload.setTravelCost(10);

        carService.update(CAR_ID, payload);

        //Then
        Car carAfter = carService.findById(1);
        Assertions.assertEquals(CAR_ID, carAfter.getId());
        Assertions.assertEquals("Mazda CX5", carAfter.getName());
        Assertions.assertEquals("AA 2222 CT", carAfter.getLicencePlate());
        Assertions.assertEquals("Vasya Pupkin", carAfter.getDriver().getUsername());
        Assertions.assertEquals(3500, carAfter.getWeightCapacity());
        Assertions.assertFalse(carAfter.isCooler());
        Assertions.assertFalse(carAfter.isAvailable());
        Assertions.assertEquals(10, carAfter.getTravelCost());
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @ExpectedDataSet("common/car/update_driver_expected.yml")
    @DisplayName("when Update Car Driver then CarDto With Updated Driver Returned")
    void whenUpdateCarDriver_thenCarDtoWithUpdatedDriverReturned() {
        // Given
        UserDto userDto = new UserDto(555,
                "Vasya Pupkin",
                "userEmail@gmail.com",
                User.UserRole.DRIVER.name(),
                true);

        CarDto payload = new CarDto();
        payload.setName("Mercedes Sprinter");
        payload.setLicencePlate("AA 1111 CT");
        payload.setDriver(userDto);
        payload.setWeightCapacity(3000);
        payload.setCooler(false);
        payload.setAvailable(true);
        payload.setTravelCost(10);

        // When
        CarDto carDto = carService.update(CAR_ID, payload);

        // Then
        Assertions.assertEquals(CAR_ID, carDto.getId());
        Assertions.assertEquals("Vasya Pupkin", carDto.getDriver().getUserName());
        Assertions.assertEquals("userEmail@gmail.com", carDto.getDriver().getEmail());
        Assertions.assertEquals(555, carDto.getDriver().getId());
        Assertions.assertEquals("DRIVER", carDto.getDriver().getRole());
    }
}
