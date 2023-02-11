package com.goodspartner.dto;

import com.goodspartner.annotations.SettingsAllocation;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SettingsAllocation(group = SettingsGroup.CLIENT, category = SettingsCategory.BUSINESS)
public class ClientBusinessPropertiesDto {
    private PrePackingDto prePacking;
    private SelfServiceDto selfService;
    private PostalDto postal;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class PrePackingDto {
        private String address;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class SelfServiceDto {
        private String address;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class PostalDto {
        private String address;
    }
}
