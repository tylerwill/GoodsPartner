package com.goodspartner.configuration.properties;

import com.goodspartner.entity.DeliveryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "client.business")
public class ClientBusinessProperties {

    // Delivery Type
    private Postal postal;
    private PrePacking prePacking;
    private SelfService selfService;
    // Car Type
    private Cooler cooler;
    // Time boundaries
    private MiddayDelivery middayDelivery;

    @Getter
    @Setter
    public static class PrePacking {
        private String address;
        private List<String> keywords;
    }

    @Getter
    @Setter
    public static class SelfService {
        private String address;
        private List<String> keywords;
    }

    @Getter
    @Setter
    public static class Postal {
        private DeliveryType deliveryType;
        private String address;
        private List<String> keywords;
    }

    @Getter
    @Setter
    public static class Cooler {
        private List<String> keywords;
    }

    @Getter
    @Setter
    public static class MiddayDelivery {
        private List<String> keywords;
    }
}
