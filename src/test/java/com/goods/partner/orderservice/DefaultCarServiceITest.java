package com.goods.partner.orderservice;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goods.partner.AbstractBaseITest;
import com.goods.partner.dto.CarDto;
import com.goods.partner.service.impl.DefaultCarService;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;

@Import(TestConfigurationToCountAllQueries.class)
@DBRider
public class DefaultCarServiceITest extends AbstractBaseITest {

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
        carDto.setStatus(false);

        carService.add(carDto);

        assertSelectCount(1);
    }

    @Test
    @DataSet("common/car/dataset_cars.yml")
    @DisplayName("validate Queries After Delete Car")
    void validateQueriesAfterDeleteCar() {
        SQLStatementCountValidator.reset();

        carService.delete(1);

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
        SQLStatementCountValidator.reset();

        carService.updateStatus(2, true);

        assertSelectCount(0);
    }
}
