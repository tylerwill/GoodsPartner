package com.goodspartner.mapper;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.configuration.properties.GoogleGeocodeProperties;
import com.goodspartner.configuration.properties.PropertyAggregate;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.ClientPropertiesDto;
import com.goodspartner.dto.GoogleGeocodePropertiesDto;
import com.goodspartner.dto.SettingsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SettingsMapperTest {

    private final String expectedClientAccountClientServerURL = "localhost:5432 YAML";
    private final String expectedClientAccountServer1CUriPrefix = "test YAML";
    private final String expectedClientAccountLogin = "bookkeeper YAML";
    private final String expectedClientAccountPassword = "password YAML";
    private final String expectedClientAccountDocumentsUriPrefix = "document/test YAML";

    private final String expectedClientBusinessPrePackingAddress = "Dnipro YAML";
    private final String expectedClientBusinessSelfServiceAddress = "Kiev YAML";
    private final String expectedClientBusinessPostalAddress = "Lviv YAML";

    private final String expectedGoogleGeocodeApiKey = "ApiKey-87987-23654asdf";

    private final String expectedClientAccountClientServerURLFe = "localhost:5432";
    private final String expectedClientAccountServer1CUriPrefixFe = "test";
    private final String expectedClientAccountLoginFe = "bookkeeper";
    private final String expectedClientAccountPasswordFe = "password";
    private final String expectedClientAccountDocumentsUriPrefixFe = "document/test";

    private final String expectedClientBusinessPrePackingAddressFe = "Dnipro";
    private final String expectedClientBusinessSelfServiceAddressFe = "Kiev";
    private final String expectedClientBusinessPostalAddressFe = "Lviv";

    private final String expectedGoogleGeocodeApiKeyFe = "ApiKey-87987-23654asdf";

    private SettingsMapper mapper;
    private PropertyAggregate propertyAggregate;


    @BeforeEach
    void setUp() {
        mapper = new SettingsMapperImpl();

        var clientAccount =  new ClientProperties();
        clientAccount.setClientServerURL(expectedClientAccountClientServerURL);
        clientAccount.setServer1CUriPrefix(expectedClientAccountServer1CUriPrefix);
        clientAccount.setLogin(expectedClientAccountLogin);
        clientAccount.setPassword(expectedClientAccountPassword);
        clientAccount.setDocumentsUriPrefix(expectedClientAccountDocumentsUriPrefix);

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

        var googleGeocode = new GoogleGeocodeProperties();
        googleGeocode.setApiKey(expectedGoogleGeocodeApiKey);

        propertyAggregate = PropertyAggregate.builder()
                .clientProperties(clientAccount)
                .clientBusinessProperties(clientBusiness)
                .googleGeocodeProperties(googleGeocode)
                .build();
    }

    @Test
    void shouldConvertPropertyAggregatorToSettingsDto() {

        SettingsDto convertedSettingsDto = mapper.mapAggregate(propertyAggregate);

        assertNotNull(convertedSettingsDto);

        ClientPropertiesDto clientAccount = convertedSettingsDto.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, clientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, clientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, clientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, clientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, clientAccount.getDocumentsUriPrefix());

        ClientBusinessPropertiesDto clientBusiness = convertedSettingsDto.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusiness.getPostal().getAddress());

        GoogleGeocodePropertiesDto googleGeocode = convertedSettingsDto.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKey, googleGeocode.getApiKey());
    }

    @Test
    void shouldUpdatePropertyEntitiesInPropertyAggregatorWithPropertiesFromSettingsDtoFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createSettingsDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientProperties clientAccount = propertyAggregate.getClientProperties();

        assertEquals(expectedClientAccountClientServerURLFe, clientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefixFe, clientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLoginFe, clientAccount.getLogin());
        assertEquals(expectedClientAccountPasswordFe, clientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefixFe, clientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, clientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties googleGeocode = propertyAggregate.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyFe, googleGeocode.getApiKey());
    }

    @Test
    void shouldNotUpdatePropertyAggregatorWhenSettingsDtoHasAllPropertiesNullFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createEmptySettingDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientProperties clientAccount = propertyAggregate.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, clientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, clientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, clientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, clientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, clientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties googleGeocode = propertyAggregate.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKey, googleGeocode.getApiKey());
    }

    @Test
    void shouldUpdatePartiallyPropertyAggregatorWhenSettingsDtoHasPartialPropertiesHaveNullValuesFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createPartialNullValueSettingsDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientProperties clientAccount = propertyAggregate.getClientProperties();

        assertEquals(expectedClientAccountClientServerURL, clientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefix, clientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLogin, clientAccount.getLogin());
        assertEquals(expectedClientAccountPassword, clientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefix, clientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddressFe, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddressFe, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddressFe, clientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties googleGeocode = propertyAggregate.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyFe, googleGeocode.getApiKey());
    }

    @Test
    void shouldUpdatePartiallyPropertyAggregatorWhenSettingsDtoHasPartialChangedValuesFromFe() {
        SettingsDto incomeSettingsDtoFromFe = createPartialChangedValuesSettingsDto();

        mapper.update(propertyAggregate, incomeSettingsDtoFromFe);

        ClientProperties clientAccount = propertyAggregate.getClientProperties();

        assertEquals(expectedClientAccountClientServerURLFe, clientAccount.getClientServerURL());
        assertEquals(expectedClientAccountServer1CUriPrefixFe, clientAccount.getServer1CUriPrefix());
        assertEquals(expectedClientAccountLoginFe, clientAccount.getLogin());
        assertEquals(expectedClientAccountPasswordFe, clientAccount.getPassword());
        assertEquals(expectedClientAccountDocumentsUriPrefixFe, clientAccount.getDocumentsUriPrefix());

        ClientBusinessProperties clientBusiness = propertyAggregate.getClientBusinessProperties();

        assertEquals(expectedClientBusinessPrePackingAddress, clientBusiness.getPrePacking().getAddress());
        assertEquals(expectedClientBusinessSelfServiceAddress, clientBusiness.getSelfService().getAddress());
        assertEquals(expectedClientBusinessPostalAddress, clientBusiness.getPostal().getAddress());

        GoogleGeocodeProperties googleGeocode = propertyAggregate.getGoogleGeocodeProperties();

        assertEquals(expectedGoogleGeocodeApiKeyFe, googleGeocode.getApiKey());
    }

    private SettingsDto createPartialChangedValuesSettingsDto() {
        var settings = new SettingsDto();
        settings.setClientProperties(createClientPropertiesDto());
        settings.setClientBusinessProperties(createClientBusinessPropertiesDtoNoChanges());
        settings.setGoogleGeocodeProperties(createGoogleGeocodePropertiesDto());

        return settings;
    }

    private SettingsDto createPartialNullValueSettingsDto() {
        var settings = new SettingsDto();
        settings.setClientProperties(new ClientPropertiesDto());
        settings.setClientBusinessProperties(createClientBusinessPropertiesDto());
        settings.setGoogleGeocodeProperties(createGoogleGeocodePropertiesDto());

        return settings;
    }

    private SettingsDto createEmptySettingDto() {
        var settingDto = new SettingsDto();

        settingDto.setClientProperties(new ClientPropertiesDto());

        var clientBusinessDto = new ClientBusinessPropertiesDto();
        clientBusinessDto.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto());
        clientBusinessDto.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto());
        clientBusinessDto.setPostal(new ClientBusinessPropertiesDto.PostalDto());

        settingDto.setClientBusinessProperties(clientBusinessDto);

        settingDto.setGoogleGeocodeProperties(new GoogleGeocodePropertiesDto());

        return settingDto;
    }

    private SettingsDto createSettingsDto() {
        var settings = new SettingsDto();
        settings.setClientProperties(createClientPropertiesDto());
        settings.setClientBusinessProperties(createClientBusinessPropertiesDto());
        settings.setGoogleGeocodeProperties(createGoogleGeocodePropertiesDto());
        return settings;
    }

    private ClientPropertiesDto createClientPropertiesDto() {
        var clientProperty = new ClientPropertiesDto();
        clientProperty.setClientServerURL(expectedClientAccountClientServerURLFe);
        clientProperty.setServer1CUriPrefix(expectedClientAccountServer1CUriPrefixFe);
        clientProperty.setLogin(expectedClientAccountLoginFe);
        clientProperty.setPassword(expectedClientAccountPasswordFe);
        clientProperty.setDocumentsUriPrefix(expectedClientAccountDocumentsUriPrefixFe);
        return clientProperty;
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

    private GoogleGeocodePropertiesDto createGoogleGeocodePropertiesDto() {
        var googleProperties = new GoogleGeocodePropertiesDto();
        googleProperties.setApiKey(expectedGoogleGeocodeApiKeyFe);
        return googleProperties;
    }
}