package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientBusinessProperties;
import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.configuration.properties.GoogleGeocodeProperties;
import com.goodspartner.configuration.properties.PropertyAggregate;
import com.goodspartner.dto.SettingsDto;
import com.goodspartner.entity.Setting;
import com.goodspartner.mapper.SettingsMapper;
import com.goodspartner.repository.SettingsRepository;
import com.goodspartner.service.SettingsCache;
import com.goodspartner.util.SettingParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class DefaultSettingsService implements SettingsCache {
    // Services
    private final SettingsRepository settingsRepository;
    private final SettingsMapper mapper;
    private final SettingParser parser;
    // Properties
    private final ClientProperties clientProperties;
    private final ClientBusinessProperties clientBusinessProperties;
    private final GoogleGeocodeProperties googleGeocodeProperties;

    @Transactional(readOnly = true)
    public void setUpCache() {
        List<Setting> settingList = settingsRepository.findAll();
        SettingsDto convertedSettingsDto = parser.getSettingsDto(settingList);
        updatePropertyEntities(convertedSettingsDto);
        log.info("Settings: {} has been initialized", settingList);
    }

    @Override
    public SettingsDto updateSettings(SettingsDto settingsDto) {
        updatePropertyEntities(settingsDto);
        List<Setting> settings = parser.getSettingsList(settingsDto);
        settingsRepository.saveAll(settings);
        log.info("Settings has been updated");
        return settingsDto;
    }

    @Override
    public SettingsDto getSettings() {
        PropertyAggregate propertyAggregate = PropertyAggregate.builder()
                .clientProperties(clientProperties)
                .clientBusinessProperties(clientBusinessProperties)
                .googleGeocodeProperties(googleGeocodeProperties)
                .build();
        return mapper.mapAggregate(propertyAggregate);
    }

    private void updatePropertyEntities(SettingsDto settingsDto) {
        PropertyAggregate aggregator = PropertyAggregate.builder()
                .clientProperties(clientProperties)
                .clientBusinessProperties(clientBusinessProperties)
                .googleGeocodeProperties(googleGeocodeProperties)
                .build();
        mapper.update(aggregator, settingsDto);
    }
}
