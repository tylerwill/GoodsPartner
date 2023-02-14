package com.goodspartner.dto;

import com.goodspartner.annotations.SettingsAllocation;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SettingsAllocation(group = SettingsGroup.CLIENT, category = SettingsCategory.ROUTING)
public class ClientRoutingPropertiesDto {

    private int unloadingTimeMinutes;
    private long maxRouteTimeMinutes;

    private LocalTime depotStartTime;
    private LocalTime depotFinishTime;

    private LocalTime defaultDeliveryStartTime;
    private LocalTime defaultDeliveryFinishTime;

    private int maxTimeProcessingSolutionSeconds;
}
