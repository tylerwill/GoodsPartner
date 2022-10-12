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

    private PostalDelivery postalDelivery;

    @Getter
    @Setter
    public static class PostalDelivery{
        private String novaPoshtaAddress;
    }
}
