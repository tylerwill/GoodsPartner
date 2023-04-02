package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.SettingsDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultSettingsServiceITest extends AbstractBaseCacheITest {

    private final String expectedClientBusinessPrePackingAddress = "Dnipro YAML";
    private final String expectedClientBusinessSelfServiceAddress = "Kiev YAML";
    private final String expectedClientBusinessPostalAddress = "Lviv YAML";

    private final String expectedClientBusinessPrePackingAddressDb = "Dnipro REV-DB";
    private final String expectedClientBusinessSelfServiceAddressDb = "Kiev REV-DB";
    private final String expectedClientBusinessPostalAddressDb = "Lviv REV-DB";

    private final String expectedClientBusinessPrePackingAddressFe = "Dnipro REV-FE";
    private final String expectedClientBusinessSelfServiceAddressFe = "Kiev REV-FE";
    private final String expectedClientBusinessPostalAddressFe = "Lviv REV-FE";


    @Test
    void checkPropertyEntitiesOnInitialization() {

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, resultClientBusiness.getPostal().getAddress());

    }

    @Test
    void whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldNotBeUpdatedWhenDatabaseIsEmpty() {
        checkPropertyEntitiesOnInitialization();

        Mockito.when(repository.findAll()).thenReturn(new ArrayList<>());

        settingsCache.setUpCache();

        Mockito.verify(repository).findAll();

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, resultClientBusiness.getPostal().getAddress());

    }

    @Test
    void whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBeUpdatedWhenDatabaseHasRecordsOfAllProperties() {
        checkPropertyEntitiesOnInitialization();

        Mockito.when(repository.findAll()).thenReturn(createSettingsListDb());

        settingsCache.setUpCache();

        Mockito.verify(repository).findAll();

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, resultClientBusiness.getPostal().getAddress());

    }

    @Test
    void whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBePartiallyUpdatedWhenDatabaseHasRecordsOfPartialProperties() {
        checkPropertyEntitiesOnInitialization();

        Mockito.when(repository.findAll()).thenReturn(createPartialSettingsListDb());

        settingsCache.setUpCache();

        Mockito.verify(repository).findAll();

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, resultClientBusiness.getPostal().getAddress());

    }

    @Test
    void shouldReturnSettingsDtoWithYamlDataWhenRequestFromFeWhenDbIsEmpty() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldNotBeUpdatedWhenDatabaseIsEmpty();

        SettingsDto resultSettingsDto = settingsCache.getSettings();

        ClientBusinessPropertiesDto clientBusinessDto = resultSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusinessDto.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusinessDto.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusinessDto.getPostal().getAddress());
    }

    @Test
    void shouldReturnSettingsDtoWithDatabaseDataWhenRequestFromFeWhenDbContainAllProperties() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBeUpdatedWhenDatabaseHasRecordsOfAllProperties();

        SettingsDto resultSettingsDto = settingsCache.getSettings();

        ClientBusinessPropertiesDto clientBusinessDto = resultSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, clientBusinessDto.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, clientBusinessDto.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, clientBusinessDto.getPostal().getAddress());

    }

    @Test
    void shouldReturnSettingsDtoWithDatabaseDataWhenRequestFromFeWhenDbContainPartialProperties() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBePartiallyUpdatedWhenDatabaseHasRecordsOfPartialProperties();

        SettingsDto resultSettingsDto = settingsCache.getSettings();

        ClientBusinessPropertiesDto clientBusinessDto = resultSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, clientBusinessDto.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, clientBusinessDto.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, clientBusinessDto.getPostal().getAddress());
    }

    @Test
    void whenFeProvideSettingsDtoWithNullPropertiesThenExistedCoincidePropertiesInCacheShouldNotBeUpdated() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldNotBeUpdatedWhenDatabaseIsEmpty();

        SettingsDto incomeSettingsDtoWithAllPropertiesNullFromFe = createEmptySettingDto();

        settingsCache.updateSettings(incomeSettingsDtoWithAllPropertiesNullFromFe);

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, resultClientBusiness.getPostal().getAddress());

    }

    @Test
    void whenFeProvideSettingsDtoWithAllPropertiesThenExistedCoincidePropertiesInCacheShouldBeUpdated() {

        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBePartiallyUpdatedWhenDatabaseHasRecordsOfPartialProperties();

        SettingsDto incomeSettingsDtoWithAllPropertiesFromFe = createSettingsDtoWithAllProperties();

        settingsCache.updateSettings(incomeSettingsDtoWithAllPropertiesFromFe);

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, resultClientBusiness.getPostal().getAddress());

    }

    @Test
    void whenFeProvideSettingsDtoWithPartialPropertiesThenExistedCoincidePropertiesInCacheShouldBeUpdatedPartially() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBeUpdatedWhenDatabaseHasRecordsOfAllProperties();

        SettingsDto incomeSettingsDtoWithPartialPropertiesFromFe = createSettingsDtoWithPartialProperties();

        settingsCache.updateSettings(incomeSettingsDtoWithPartialPropertiesFromFe);

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, resultClientBusiness.getPostal().getAddress());

    }
}