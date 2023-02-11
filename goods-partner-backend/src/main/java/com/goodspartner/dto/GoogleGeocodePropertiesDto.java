package com.goodspartner.dto;

import com.goodspartner.annotations.SettingsAllocation;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@SettingsAllocation(group = SettingsGroup.GOOGLE, category = SettingsCategory.GEOCODE)
public class GoogleGeocodePropertiesDto {
    private String apiKey;
}
