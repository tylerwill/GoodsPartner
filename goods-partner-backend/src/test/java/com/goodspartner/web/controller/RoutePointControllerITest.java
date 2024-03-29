package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.dto.Coordinates;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.vladmihalcea.sql.SQLStatementCountValidator.assertInsertCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@Import(TestConfigurationToCountAllQueries.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "goodspartner.security.enabled=true")
class RoutePointControllerITest extends AbstractWebITest {

    private static final String URI = "/api/v1/route-points/7559900/coordinates";


    @Test
    @WithMockUser(roles = "DRIVER")
    @DataSet(value = "datasets/common/update_client_coordinates/routes_dataset.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    @ExpectedDataSet(value = "datasets/common/update_client_coordinates/dataset_routes_expected.yml")
    @DisplayName("when Update Client Coordinates then Coordinates Successful Updated And OK Status Returned")
    void whenUpdateCoordinates_thenOkStatusReturned() throws Exception {
        SQLStatementCountValidator.reset();
        // Given
        Coordinates coordinates = new Coordinates(50.46946, 30.50268);
        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .put(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coordinates)))
                .andExpect(status().isOk());
        // Then
        assertSelectCount(5);  // One for RoutePoint + One AddressExternal + One for Route & Car & User & Store + One for User + One for Driver.
        assertUpdateCount(2); // One for AddressExternal.
        assertInsertCount(1);  // One for DeliveryHistory.
    }

}
