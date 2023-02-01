package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.dto.AddressExternalDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityDisableConfig.class})
public class AddressControllerITest extends AbstractWebITest {
    private static final String ADDRESSES_ENDPOINT = "/api/v1/addresses";
    private static final String ADDRESSES_DATASET = "datasets/addresses/addresses-dataset.json";

    @Test
    @DataSet(value = ADDRESSES_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void testFindAllShouldReturnAddressExternalDtoAndHttpStatusOK() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(ADDRESSES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/addresses/addresses-get-all.json")));
    }

    @Test
    @DataSet(value = ADDRESSES_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void testUpdateShouldUpdateAddressExternalInDbThenReturnUpdatedAddressExternalDtoAndHttpStatusAcceptedWhenIdIsPresent() throws Exception {
        AddressExternalDto addressExternalDtoToUpdate = createDtoWithIdIsPresent();

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put(ADDRESSES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(addressExternalDtoToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/addresses/address-dto-for-update.json")));
    }

    @Test
    @DataSet(value = ADDRESSES_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void testUpdateShouldReturnHttpStatusNotFoundWhenProvidedIdIsAbsent() throws Exception {
        AddressExternalDto addressExternalDtoToUpdate = createDtoWithIdIsAbsent();

        mockMvc
                .perform(MockMvcRequestBuilders
                        .put(ADDRESSES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(addressExternalDtoToUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DataSet(value = ADDRESSES_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void testDeleteShouldReturnHttpStatusOkWhenProvidedIdIsPresent() throws Exception {
        AddressExternalDto addressExternalDtoToDelete = createDtoWithIdIsPresent();

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete(ADDRESSES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(addressExternalDtoToDelete)))
                .andExpect(status().isOk());
    }

    @Test
    @DataSet(value = ADDRESSES_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void testDeleteShouldReturnHttpStatusNotFoundWhenProvidedIdIsAbsent() throws Exception {
        AddressExternalDto addressExternalDtoToDelete = createDtoWithIdIsAbsent();

        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete(ADDRESSES_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(addressExternalDtoToDelete)))
                .andExpect(status().isNotFound());
    }

    private AddressExternalDto createDtoWithIdIsPresent() {
        return new AddressExternalDto(
                "Київ, вул. Б. Хмеьницького, 40   Тетяна, 050-775-99-40",
                "Лила Кейк ",
                "KNOWN",
                "вулиця Далекий Гай, 40, Київ, Україна, 02000",
                80.4469685,
                90.50906
        );
    }

    private AddressExternalDto createDtoWithIdIsAbsent() {
        return new AddressExternalDto(
                "Київ, вул. ",
                "Лила",
                "KNOWN",
                "вулиця Далекий Гай, 40, Київ, Україна, 02000",
                80.4469685,
                90.50906
        );
    }
}
