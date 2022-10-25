package com.goodspartner.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "grandedolce.business")
public class GrandeDolceBusinessProperties {

    private PrePacking prePacking;

    @Getter
    @Setter
    public static class PrePacking {
        private String address;
    }
}
