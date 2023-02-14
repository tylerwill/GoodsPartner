package com.goodspartner.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "client.routing")
public class ClientRoutingProperties {

    private int unloadingTimeMinutes;
    private long maxRouteTimeMinutes;

    private LocalTime depotStartTime;
    private LocalTime depotFinishTime;

    private LocalTime defaultDeliveryStartTime;
    private LocalTime defaultDeliveryFinishTime;

    private int maxTimeProcessingSolutionSeconds;

    // Normalization requires to provide recalculation between absolute drive time, and relative arrival time
    public long getNormalizationTimeMinutes() {
        return depotStartTime.getHour() * 60L;
    }
}
