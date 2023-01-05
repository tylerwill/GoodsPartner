package com.goodspartner.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "google.geocode")
public class GoogleGeocodeProperties {

    private String apiKey;
    private String region;
    private String language;

    @NestedConfigurationProperty
    private Boundaries boundaries;

    @Getter
    @Setter
    public static class Boundaries {
        private double north;
        private double south;
        private double east;
        private double west;
    }

}
