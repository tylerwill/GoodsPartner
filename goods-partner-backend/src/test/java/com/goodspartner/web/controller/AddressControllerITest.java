package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.AddressStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "goodspartner.security.enabled=true")
public class AddressControllerITest extends AbstractWebITest {
    private static final String ADDRESSES_ENDPOINT = "/api/v1/addresses";
    private static final String ADDRESSES_DATASET = "datasets/addresses/addresses-dataset.json";

    @Test
    @WithMockUser(roles = "LOGISTICIAN")
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
    @WithMockUser(roles = "LOGISTICIAN")
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
    @WithMockUser(roles = "LOGISTICIAN")
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
    @WithMockUser(roles = "LOGISTICIAN")
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
    @WithMockUser(roles = "LOGISTICIAN")
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
        MapPoint mapPoint = MapPoint.builder()
                .address("вулиця Далекий Гай, 40, Київ, Україна, 02000")
                .longitude(80.4469685)
                .latitude(90.50906)
                .status(AddressStatus.KNOWN)
                .build();
        return new AddressExternalDto(
                "Київ, вул. Б. Хмеьницького, 40   Тетяна, 050-775-99-40",
                "Лила Кейк ",
                mapPoint);
    }

    private AddressExternalDto createDtoWithIdIsAbsent() {
        MapPoint mapPoint = MapPoint.builder()
                .address("вулиця Далекий Гай, 40, Київ, Україна, 02000")
                .longitude(80.4469685)
                .latitude(90.50906)
                .status(AddressStatus.KNOWN)
                .build();
        return new AddressExternalDto(
                "Київ, вул. ",
                "Лила",
                mapPoint);
    }
}
