package com.goodspartner.dto;

import com.goodspartner.annotations.SettingsAllocation;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SettingsAllocation(group = SettingsGroup.CLIENT, category = SettingsCategory.ACCOUNTING)
public class ClientPropertiesDto {
    private String clientServerURL;
    private String server1CUriPrefix;
    private String login;
    private String password;
    private String documentsUriPrefix;
}
