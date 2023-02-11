package com.goodspartner.service;

import com.goodspartner.dto.SettingsDto;

public interface SettingsCache {

    void setUpCache();

    SettingsDto updateSettings(SettingsDto settingsDto);

    SettingsDto getSettings();

}
