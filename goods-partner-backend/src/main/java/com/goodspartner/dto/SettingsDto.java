package com.goodspartner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SettingsDto {
    private ClientPropertiesDto clientProperties;
    private ClientBusinessPropertiesDto clientBusinessProperties;
    private GoogleGeocodePropertiesDto googleGeocodeProperties;
}
