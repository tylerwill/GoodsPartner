package com.goodspartner.service.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.service.OrderService;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;

@Import(TestConfigurationToCountAllQueries.class)
@DBRider
public class DefaultOrderServiceITest extends AbstractBaseITest {

    @Autowired
    private OrderService orderService;

    @Test
    @DataSet("common/dataset.yml")
    @DisplayName("Check queries quantity correct after calculate orders")
    void validateQueriesAfterCalculateOrders() {
        SQLStatementCountValidator.reset();

        orderService.findAllByShippingDate(LocalDate.of(2022, 7, 12));

        assertSelectCount(2);
    }
}
