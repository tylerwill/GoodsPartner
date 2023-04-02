package com.goodspartner.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.configuration.properties.ClientRoutingProperties;
import com.goodspartner.configuration.properties.GoogleGeocodeProperties;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.ClientRoutingPropertiesDto;
import com.goodspartner.dto.GoogleGeocodePropertiesDto;
import com.goodspartner.dto.SettingsDto;
import com.goodspartner.entity.Setting;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import com.goodspartner.mapper.SettingsMapper;
import com.goodspartner.mapper.SettingsMapperImpl;
import com.goodspartner.repository.SettingsRepository;
import com.goodspartner.util.SettingParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AbstractBaseCacheITest {
    private final static String CLIENT_BUSINESS_PROP_DB = "{\"prePacking\": {  \"address\": \"Dnipro REV-DB\"},\"selfService\": {  \"address\": \"Kiev REV-DB\"},\"postal\": {  \"address\": \"Lviv REV-DB\"}}";

    @Mock
    protected SettingsRepository repository;
    protected DefaultSettingsService settingsCache;

    @BeforeEach
    void setUp() {

        SettingsMapper mapper = new SettingsMapperImpl();

        SettingParser parser = new SettingParser(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

        var clientAccount =  new ClientProperties();
        clientAccount.setClientServerURL("localhost:5432 YAML");
        clientAccount.setServer1CUriPrefix("test YAML");
        clientAccount.setLogin("bookkeeper YAML");
        clientAccount.setPassword("password YAML");
        clientAccount.setDocumentsUriPrefix("document/test YAML");

        var clientRouting = new ClientRoutingProperties();
        // TODO add props

        var clientBusiness = new ClientBusinessProperties();
        var prePacing = new ClientBusinessProperties.PrePacking();
        prePacing.setAddress("Dnipro YAML");
        clientBusiness.setPrePacking(prePacing);
        var selfService = new ClientBusinessProperties.SelfService();
        selfService.setAddress("Kiev YAML");
        clientBusiness.setSelfService(selfService);
        var postal = new ClientBusinessProperties.Postal();
        postal.setAddress("Lviv YAML");
        clientBusiness.setPostal(postal);

        var googleGeocode = new GoogleGeocodeProperties();
        googleGeocode.setApiKey("ApiKey-87987-23654asdf YAML");

        settingsCache = new DefaultSettingsService(repository, mapper, parser,
                clientRouting,
                clientBusiness);
    }

    protected List<Setting> createSettingsListDb() {
        return List.of(createSettingsInstance(SettingsGroup.CLIENT, SettingsCategory.BUSINESS, CLIENT_BUSINESS_PROP_DB));
    }

    protected List<Setting> createPartialSettingsListDb() {
        return List.of(createSettingsInstance(SettingsGroup.CLIENT, SettingsCategory.BUSINESS, CLIENT_BUSINESS_PROP_DB));
    }
    protected SettingsDto createEmptySettingDto() {
        var settingDto = new SettingsDto();

        var clientBusinessDto = new ClientBusinessPropertiesDto();
        clientBusinessDto.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto());
        clientBusinessDto.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto());
        clientBusinessDto.setPostal(new ClientBusinessPropertiesDto.PostalDto());
        settingDto.setClientBusinessProperties(clientBusinessDto);

        settingDto.setClientRoutingProperties(new ClientRoutingPropertiesDto());

        return settingDto;
    }

    protected Setting createSettingsInstance(SettingsGroup group, SettingsCategory category, String property) {
        var settings = new Setting();
        settings.setSettingKey(new Setting.SettingKey(group, category));
        settings.setProperties(property + group.getValue());
        return settings;
    }

    protected SettingsDto createSettingsDtoWithPartialProperties() {
        var settingDto = new SettingsDto();
        settingDto.setClientRoutingProperties(createClientRoutingProperties());
        settingDto.setClientBusinessProperties(createClientBusinessPropertiesDto());
        return settingDto;
    }

    protected SettingsDto createSettingsDtoWithAllProperties() {
        var settingDto = new SettingsDto();
        settingDto.setClientRoutingProperties(createClientRoutingProperties());
        settingDto.setClientBusinessProperties(createClientBusinessPropertiesDto());

        return settingDto;
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
        clientBusiness.setPrePacking(new ClientBusinessPropertiesDto.PrePackingDto("Dnipro REV-FE", new ArrayList<>()));
        clientBusiness.setSelfService(new ClientBusinessPropertiesDto.SelfServiceDto("Kiev REV-FE", new ArrayList<>()));
        clientBusiness.setPostal(new ClientBusinessPropertiesDto.PostalDto("Lviv REV-FE", new ArrayList<>()));

        return clientBusiness;
    }

    private GoogleGeocodePropertiesDto createGoogleGeocodePropertiesDto() {
        var googleProperties = new GoogleGeocodePropertiesDto();
        googleProperties.setApiKey("ApiKey-87987-23654asdf REV-FE");

        return googleProperties;
    }
}
