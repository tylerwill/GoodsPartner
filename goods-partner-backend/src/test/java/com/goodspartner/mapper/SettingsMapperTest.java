package com.goodspartner.mapper;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.configuration.properties.PropertyAggregate;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.SettingsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SettingsMapperTest {

    private final String expectedClientBusinessPrePackingAddress = "Dnipro YAML";
    private final String expectedClientBusinessSelfServiceAddress = "Kiev YAML";
    private final String expectedClientBusinessPostalAddress = "Lviv YAML";

    private final String expectedClientBusinessPrePackingAddressFe = "Dnipro";
    private final String expectedClientBusinessSelfServiceAddressFe = "Kiev";
    private final String expectedClientBusinessPostalAddressFe = "Lviv";

    private SettingsMapper mapper;
    private PropertyAggregate propertyAggregate;


    @BeforeEach
    void setUp() {
        mapper = new SettingsMapperImpl();

        var clientBusiness = new ClientBusinessProperties();
        var prePacing = new ClientBusinessProperties.PrePacking();
        prePacing.setAddress(expectedClientBusinessPrePackingAddress);
        clientBusiness.setPrePacking(prePacing);
        var selfService = new ClientBusinessProperties.SelfService();
        selfService.setAddress(expectedClientBusinessSelfServiceAddress);
        clientBusiness.setSelfService(selfService);
        var postal = new ClientBusinessProperties.Postal();
        postal.setAddress(expectedClientBusinessPostalAddress);
        clientBusiness.setPostal(postal);

        propertyAggregate = PropertyAggregate.builder()
                .clientBusinessProperties(clientBusiness)
                .build();
    }

    @Test
    void shouldConvertPropertyAggregatorToSettingsDto() {

        SettingsDto convertedSettingsDto = mapper.mapAggregate(propertyAggregate);

        assertNotNull(convertedSettingsDto);

        ClientBusinessPropertiesDto clientBusiness = convertedSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusiness.getPostal().getAddress());
    }

    @Test
    void shouldUpdatePropertyEntitiesInPropertyAggregatorWithPropertiesFromSettingsDtoFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createSettingsDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, clientBusiness.getPostal().getAddress());
    }

    @Test
    void shouldNotUpdatePropertyAggregatorWhenSettingsDtoHasAllPropertiesNullFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createEmptySettingDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusiness.getPostal().getAddress());
    }

    @Test
    void shouldUpdatePartiallyPropertyAggregatorWhenSettingsDtoHasPartialPropertiesHaveNullValuesFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createPartialNullValueSettingsDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, clientBusiness.getPostal().getAddress());
    }

    @Test
    void shouldUpdatePartiallyPropertyAggregatorWhenSettingsDtoHasPartialChangedValuesFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createPartialChangedValuesSettingsDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusiness.getPostal().getAddress());
    }

    private SettingsDto createPartialChangedValuesSettingsDto() {
        var settings = new SettingsDto();
        settings.setClientBusinessProperties(createClientBusinessPropertiesDtoNoChanges());
        return settings;
    }

    private SettingsDto createPartialNullValueSettingsDto() {
        var settings = new SettingsDto();
        settings.setClientBusinessProperties(createClientBusinessPropertiesDto());
        return settings;
    }

    private SettingsDto createEmptySettingDto() {
        var settingDto = new SettingsDto();

        var clientBusinessDto = new ClientBusinessPropertiesDto();
        clientBusinessDto.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto());
        clientBusinessDto.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto());
        clientBusinessDto.setPostal(new ClientBusinessPropertiesDto.PostalDto());

        settingDto.setClientBusinessProperties(clientBusinessDto);

        return settingDto;
    }

    private SettingsDto createSettingsDto() {
        var settings = new SettingsDto();
        settings.setClientBusinessProperties(createClientBusinessPropertiesDto());
        return settings;
    }

    private ClientBusinessPropertiesDto createClientBusinessPropertiesDtoNoChanges() {
        var clientBusiness = new ClientBusinessPropertiesDto();
        clientBusiness.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto(expectedClientBusinessPrePackingAddress, new ArrayList<>()));
        clientBusiness.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto(expectedClientBusinessSelfServiceAddress, new ArrayList<>()));
        clientBusiness.setPostal(new ClientBusinessPropertiesDto.PostalDto(expectedClientBusinessPostalAddress, new ArrayList<>()));
        return clientBusiness;
    }

    private ClientBusinessPropertiesDto createClientBusinessPropertiesDto() {
        var clientBusiness = new ClientBusinessPropertiesDto();
        clientBusiness.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto(expectedClientBusinessPrePackingAddressFe, new ArrayList<>()));
        clientBusiness.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto(expectedClientBusinessSelfServiceAddressFe, new ArrayList<>()));
        clientBusiness.setPostal(new ClientBusinessPropertiesDto.PostalDto(expectedClientBusinessPostalAddressFe, new ArrayList<>()));
        return clientBusiness;
    }
}