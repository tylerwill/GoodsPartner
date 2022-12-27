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
@ConfigurationProperties(prefix = "client.business")
public class ClientBusinessProperties {

    private PrePacking prePacking;
    private SelfService selfService;
    private Postal postal;

    @Getter
    @Setter
    public static class PrePacking {
        private String address;
    }

    @Getter
    @Setter
    public static class SelfService {
        private String address;
    }

    @Getter
    @Setter
    public static class Postal {
        private String address;
    }
}
