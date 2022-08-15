package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.dto.CarDto;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;

@Import(TestConfigurationToCountAllQueries.class)
@DBRider
public class DefaultCarServiceITest extends AbstractBaseITest {

    private static final int CAR_ID = 1;

    @Autowired
    private DefaultCarService carService;

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("validate Queries After Add Car")
    void validateQueriesAfterAddCar() {
        SQLStatementCountValidator.reset();

        CarDto carDto = new CarDto();
        carDto.setName("DAF");
        carDto.setDriver("Roman Levchenko");
        carDto.setCooler(true);
        carDto.setLicencePlate("AA 4567 CT");
        carDto.setWeightCapacity(5000);
        carDto.setAvailable(false);

        carService.add(carDto);

        assertSelectCount(1);
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("validate Queries After Delete Car")
    void validateQueriesAfterDeleteCar() {
        SQLStatementCountValidator.reset();

        carService.delete(CAR_ID);

        assertSelectCount(1);
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("validate Queries after Find Al lCars")
    void validateQueries_afterFindAllCars() {
        SQLStatementCountValidator.reset();

        carService.findAll();

        assertSelectCount(1);
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("validate Queries AfterUpdate Car Status")
    void validateQueries_afterUpdateCarStatus() {
        // Given
        CarDto carBefore = carService.getById(1);
        Assertions.assertEquals("Mercedes Sprinter", carBefore.getName());
        Assertions.assertEquals("AA 1111 CT", carBefore.getLicencePlate());
        Assertions.assertEquals("Oleg Dudka", carBefore.getDriver());
        Assertions.assertEquals(3000, carBefore.getWeightCapacity());
        Assertions.assertEquals(false, carBefore.getCooler());
        Assertions.assertEquals(true, carBefore.getAvailable());
        Assertions.assertEquals(0.0, carBefore.getLoadSize());
        Assertions.assertEquals(10, carBefore.getTravelCost());

        // When
        CarDto payload = new CarDto();
        payload.setAvailable(false);
        payload.setName("Mazda CX5");
        payload.setLicencePlate("AA 2222 CT");
        payload.setDriver("Vasya Pupkin");
        payload.setWeightCapacity(3500);
        payload.setCooler(false);
        payload.setAvailable(false);
        payload.setLoadSize(0.0);
        payload.setTravelCost(10);

        carService.update(CAR_ID, payload);

        //Then
        CarDto carAfter = carService.getById(1);
        Assertions.assertEquals(CAR_ID, carAfter.getId());
        Assertions.assertEquals("Mazda CX5", carAfter.getName());
        Assertions.assertEquals("AA 2222 CT", carAfter.getLicencePlate());
        Assertions.assertEquals("Vasya Pupkin", carAfter.getDriver());
        Assertions.assertEquals(3500, carAfter.getWeightCapacity());
        Assertions.assertEquals(false, carAfter.getCooler());
        Assertions.assertEquals(false, carAfter.getAvailable());
        Assertions.assertEquals(0.0, carAfter.getLoadSize());
        Assertions.assertEquals(10, carAfter.getTravelCost());
    }
}
