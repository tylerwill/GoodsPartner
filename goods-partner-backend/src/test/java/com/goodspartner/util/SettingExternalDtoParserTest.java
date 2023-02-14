package com.goodspartner.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.ClientPropertiesDto;
import com.goodspartner.dto.ClientRoutingPropertiesDto;
import com.goodspartner.dto.GoogleGeocodePropertiesDto;
import com.goodspartner.dto.SettingsDto;
import com.goodspartner.entity.Setting;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingExternalDtoParserTest {

    private final String expectedClientPropertiesDtoProperties = "{\"clientServerURL\":\"localhost:5432\",\"server1CUriPrefix\":\"test\",\"login\":\"bookkeeper\",\"password\":\"password\",\"documentsUriPrefix\":\"document/test\"}";
    private final String expectedClientBusinessPropertiesDtoProperties = "{\"prePacking\":{\"address\":\"Dnipro\",\"keywords\":[]},\"selfService\":{\"address\":\"Kiev\",\"keywords\":[]},\"postal\":{\"address\":\"Lviv\",\"keywords\":[]}}";
    private final String expectedClientRoutingPropertiesDtoProperties = "{\"unloadingTimeMinutes\":15,\"maxRouteTimeMinutes\":500,\"depotStartTime\":\"08:00:00\",\"depotFinishTime\":\"09:00:00\",\"defaultDeliveryStartTime\":\"09:00:00\",\"defaultDeliveryFinishTime\":\"18:00:00\",\"maxTimeProcessingSolutionSeconds\":20}";

    private final SettingParser parser = new SettingParser(
            new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

    @Test
    void shouldReturnEligibleSettingBasedOnClientPropertiesDto() {
        ClientPropertiesDto incomeSetting = createClientPropertiesDto();

        Setting settingExternal = parser.toSetting(incomeSetting);

        assertEquals(SettingsGroup.CLIENT, settingExternal.getSettingKey().getGroup());
        assertEquals(SettingsCategory.ACCOUNTING, settingExternal.getSettingKey().getCategory());
        assertEquals(expectedClientPropertiesDtoProperties, settingExternal.getProperties());
    }

    @Test
    void shouldReturnEligibleSettingBasedOnClientBusinessPropertiesDto() {
        ClientBusinessPropertiesDto incomeSetting = createClientBusinessPropertiesDto();

        Setting settingExternal = parser.toSetting(incomeSetting);

        assertEquals(SettingsGroup.CLIENT, settingExternal.getSettingKey().getGroup());
        assertEquals(SettingsCategory.BUSINESS, settingExternal.getSettingKey().getCategory());
        assertEquals(expectedClientBusinessPropertiesDtoProperties, settingExternal.getProperties());
    }

    @Test
    void shouldReturnListOfSettingsParsingSettingsDto() {
        SettingsDto incomeSettingsDto = createSettingsDto();

        List<Setting> resultList = parser.getSettingsList(incomeSettingsDto);
        String resultListAsString = resultList.toString();

        assertNotNull(resultList);
        assertEquals(4, resultList.size());

        assertTrue(resultListAsString.contains(expectedClientPropertiesDtoProperties));
        assertTrue(resultListAsString.contains(expectedClientBusinessPropertiesDtoProperties));
        assertTrue(resultListAsString.contains(expectedClientRoutingPropertiesDtoProperties));
    }

    @Test
    void shouldConvertSettingsListToSettingsDto() {
        SettingsDto incomeSettingsDto = createSettingsDto();

        List<Setting> resultExternalList = parser.getSettingsList(incomeSettingsDto);

        SettingsDto resultSettingsDto = parser.getSettingsDto(resultExternalList);

        assertNotNull(resultSettingsDto);
        assertEquals(incomeSettingsDto.getClientProperties().getClientServerURL(), resultSettingsDto.getClientProperties().getClientServerURL());
        assertEquals(incomeSettingsDto.getClientBusinessProperties().getPrePacking().getAddress(), resultSettingsDto.getClientBusinessProperties().getPrePacking().getAddress());
        assertEquals(incomeSettingsDto.getGoogleGeocodeProperties().getApiKey(), resultSettingsDto.getGoogleGeocodeProperties().getApiKey());
    }

    private SettingsDto createSettingsDto() {
        var settingDto = new SettingsDto();
        settingDto.setClientProperties(createClientPropertiesDto());
        settingDto.setClientRoutingProperties(createClientRoutingProperties());
        settingDto.setClientBusinessProperties(createClientBusinessPropertiesDto());
        settingDto.setGoogleGeocodeProperties(createGoogleGeocodePropertiesDto());

        return settingDto;
    }

    private ClientPropertiesDto createClientPropertiesDto() {
        var clientProperty = new ClientPropertiesDto();
        clientProperty.setClientServerURL("localhost:5432");
        clientProperty.setServer1CUriPrefix("test");
        clientProperty.setLogin("bookkeeper");
        clientProperty.setPassword("password");
        clientProperty.setDocumentsUriPrefix("document/test");

        return clientProperty;
    }

    private ClientRoutingPropertiesDto createClientRoutingProperties() {
        var clientRoutingProperties = new ClientRoutingPropertiesDto();
        clientRoutingProperties.setUnloadingTimeMinutes(15);
        clientRoutingProperties.setMaxRouteTimeMinutes(500);
        clientRoutingProperties.setDepotStartTime(LocalTime.of(8, 0));
        clientRoutingProperties.setDepotFinishTime(LocalTime.of(9, 0));
        clientRoutingProperties.setDefaultDeliveryStartTime(LocalTime.of(9, 0));
        clientRoutingProperties.setDefaultDeliveryFinishTime(LocalTime.of(18, 0));
        clientRoutingProperties.setMaxTimeProcessingSolutionSeconds(20);
        return clientRoutingProperties;
    }

    private ClientBusinessPropertiesDto createClientBusinessPropertiesDto() {
        var clientBusiness = new ClientBusinessPropertiesDto();
        clientBusiness.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto("Dnipro", new ArrayList<>()));
        clientBusiness.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto("Kiev", new ArrayList<>()));
        clientBusiness.setPostal(new ClientBusinessPropertiesDto.PostalDto("Lviv", new ArrayList<>()));

        return clientBusiness;
    }

    private GoogleGeocodePropertiesDto createGoogleGeocodePropertiesDto() {
        var googleProperties = new GoogleGeocodePropertiesDto();
        googleProperties.setApiKey("ApiKey-87987-23654asdf");

        return googleProperties;
    }
}