package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.configuration.properties.GoogleGeocodeProperties;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.ClientPropertiesDto;
import com.goodspartner.dto.GoogleGeocodePropertiesDto;
import com.goodspartner.dto.SettingsDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultSettingsServiceITest extends AbstractBaseCacheITest {

    private final String expectedClientAccountClientServerURL = "localhost:5432 YAML";
    private final String expectedClientAccountServer1CUriPrefix = "test YAML";
    private final String expectedClientAccountLogin = "bookkeeper YAML";
    private final String expectedClientAccountPassword = "password YAML";
    private final String expectedClientAccountDocumentsUriPrefix = "document/test YAML";

    private final String expectedClientBusinessPrePackingAddress = "Dnipro YAML";
    private final String expectedClientBusinessSelfServiceAddress = "Kiev YAML";
    private final String expectedClientBusinessPostalAddress = "Lviv YAML";

    private final String expectedGoogleGeocodeApiKey = "ApiKey-87987-23654asdf YAML";

    private final String expectedClientAccountClientServerURLDb = "localhost:5432 REV-DB";
    private final String expectedClientAccountServer1CUriPrefixDb = "test REV-DB";
    private final String expectedClientAccountLoginDb = "bookkeeper REV-DB";
    private final String expectedClientAccountPasswordDb = "password REV-DB";
    private final String expectedClientAccountDocumentsUriPrefixDb = "document/test REV-DB";

    private final String expectedClientBusinessPrePackingAddressDb = "Dnipro REV-DB";
    private final String expectedClientBusinessSelfServiceAddressDb = "Kiev REV-DB";
    private final String expectedClientBusinessPostalAddressDb = "Lviv REV-DB";

    private final String expectedGoogleGeocodeApiKeyDb = "ApiKey-87987-23654asdf REV-DB";

    private final String expectedClientBusinessPrePackingAddressFe = "Dnipro REV-FE";
    private final String expectedClientBusinessSelfServiceAddressFe = "Kiev REV-FE";
    private final String expectedClientBusinessPostalAddressFe = "Lviv REV-FE";

    private final String expectedGoogleGeocodeApiKeyFe = "ApiKey-87987-23654asdf REV-FE";

    @Test
    void checkPropertyEntitiesOnInitialization() {
        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKey, resultGoogleGeocode.getApiKey());
    }

    @Test
    void whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldNotBeUpdatedWhenDatabaseIsEmpty() {
        checkPropertyEntitiesOnInitialization();

        Mockito.when(repository.findAll()).thenReturn(new ArrayList<>());

        settingsCache.setUpCache();

        Mockito.verify(repository).findAll();

        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKey, resultGoogleGeocode.getApiKey());
    }

    @Test
    void whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBeUpdatedWhenDatabaseHasRecordsOfAllProperties() {
        checkPropertyEntitiesOnInitialization();

        Mockito.when(repository.findAll()).thenReturn(createSettingsListDb());

        settingsCache.setUpCache();

        Mockito.verify(repository).findAll();

        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURLDb, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefixDb, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLoginDb, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPasswordDb, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefixDb, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyDb, resultGoogleGeocode.getApiKey());
    }

    @Test
    void whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBePartiallyUpdatedWhenDatabaseHasRecordsOfPartialProperties() {
        checkPropertyEntitiesOnInitialization();

        Mockito.when(repository.findAll()).thenReturn(createPartialSettingsListDb());

        settingsCache.setUpCache();

        Mockito.verify(repository).findAll();

        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyDb, resultGoogleGeocode.getApiKey());
    }

    @Test
    void shouldReturnSettingsDtoWithYamlDataWhenRequestFromFeWhenDbIsEmpty() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldNotBeUpdatedWhenDatabaseIsEmpty();

        SettingsDto resultSettingsDto = settingsCache.getSettings();

        ClientPropertiesDto clientAccountDto = resultSettingsDto.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, clientAccountDto.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, clientAccountDto.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, clientAccountDto.getLogin());
        assertEquals(expectedClientAccountPassword, clientAccountDto.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, clientAccountDto.getDocumentsUriPrefix());

        ClientBusinessPropertiesDto clientBusinessDto = resultSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusinessDto.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusinessDto.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusinessDto.getPostal().getAddress());

        GoogleGeocodePropertiesDto googleGeocodeDto = resultSettingsDto.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKey, googleGeocodeDto.getApiKey());
    }

    @Test
    void shouldReturnSettingsDtoWithDatabaseDataWhenRequestFromFeWhenDbContainAllProperties() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBeUpdatedWhenDatabaseHasRecordsOfAllProperties();

        SettingsDto resultSettingsDto = settingsCache.getSettings();

        ClientPropertiesDto clientAccountDto = resultSettingsDto.getClientProperties();

        assertEquals(expectedClientAccountClientServerURLDb, clientAccountDto.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefixDb, clientAccountDto.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLoginDb, clientAccountDto.getLogin());
        assertEquals(expectedClientAccountPasswordDb, clientAccountDto.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefixDb, clientAccountDto.getDocumentsUriPrefix());

        ClientBusinessPropertiesDto clientBusinessDto = resultSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, clientBusinessDto.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, clientBusinessDto.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, clientBusinessDto.getPostal().getAddress());

        GoogleGeocodePropertiesDto googleGeocodeDto = resultSettingsDto.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyDb, googleGeocodeDto.getApiKey());
    }

    @Test
    void shouldReturnSettingsDtoWithDatabaseDataWhenRequestFromFeWhenDbContainPartialProperties() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBePartiallyUpdatedWhenDatabaseHasRecordsOfPartialProperties();

        SettingsDto resultSettingsDto = settingsCache.getSettings();

        ClientPropertiesDto clientAccountDto = resultSettingsDto.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, clientAccountDto.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, clientAccountDto.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, clientAccountDto.getLogin());
        assertEquals(expectedClientAccountPassword, clientAccountDto.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, clientAccountDto.getDocumentsUriPrefix());

        ClientBusinessPropertiesDto clientBusinessDto = resultSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressDb, clientBusinessDto.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressDb, clientBusinessDto.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressDb, clientBusinessDto.getPostal().getAddress());

        GoogleGeocodePropertiesDto googleGeocodeDto = resultSettingsDto.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyDb, googleGeocodeDto.getApiKey());
    }

    @Test
    void whenFeProvideSettingsDtoWithNullPropertiesThenExistedCoincidePropertiesInCacheShouldNotBeUpdated() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldNotBeUpdatedWhenDatabaseIsEmpty();

        SettingsDto incomeSettingsDtoWithAllPropertiesNullFromFe = createEmptySettingDto();

        settingsCache.updateSettings(incomeSettingsDtoWithAllPropertiesNullFromFe);

        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKey, resultGoogleGeocode.getApiKey());
    }

    @Test
    void whenFeProvideSettingsDtoWithAllPropertiesThenExistedCoincidePropertiesInCacheShouldBeUpdated() {
        String expectedClientAccountClientServerURLFe = "localhost:5432 REV-FE";
        String expectedClientAccountServer1CUriPrefixFe = "test REV-FE";
        String expectedClientAccountLoginFe = "bookkeeper REV-FE";
        String expectedClientAccountPasswordFe = "password REV-FE";
        String expectedClientAccountDocumentsUriPrefixFe = "document/test REV-FE";

        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBePartiallyUpdatedWhenDatabaseHasRecordsOfPartialProperties();

        SettingsDto incomeSettingsDtoWithAllPropertiesFromFe = createSettingsDtoWithAllProperties();

        settingsCache.updateSettings(incomeSettingsDtoWithAllPropertiesFromFe);

        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURLFe, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefixFe, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLoginFe, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPasswordFe, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefixFe, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyFe, resultGoogleGeocode.getApiKey());
    }

    @Test
    void whenFeProvideSettingsDtoWithPartialPropertiesThenExistedCoincidePropertiesInCacheShouldBeUpdatedPartially() {
        whenAppStartsUpPropertyEntitiesWithDataFromYamlShouldBeUpdatedWhenDatabaseHasRecordsOfAllProperties();

        SettingsDto incomeSettingsDtoWithPartialPropertiesFromFe = createSettingsDtoWithPartialProperties();

        settingsCache.updateSettings(incomeSettingsDtoWithPartialPropertiesFromFe);

        ClientProperties resultClientAccount = settingsCache.getClientProperties();

        assertEquals(expectedClientAccountClientServerURLDb, resultClientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefixDb, resultClientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLoginDb, resultClientAccount.getLogin());
        assertEquals(expectedClientAccountPasswordDb, resultClientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefixDb, resultClientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties resultClientBusiness = settingsCache.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, resultClientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, resultClientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, resultClientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties resultGoogleGeocode = settingsCache.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyFe, resultGoogleGeocode.getApiKey());
    }
}