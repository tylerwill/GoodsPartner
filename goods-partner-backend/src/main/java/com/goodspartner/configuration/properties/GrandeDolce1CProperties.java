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
@ConfigurationProperties(prefix = "grandedolce.accounting-app")
public class GrandeDolce1CProperties {

    private String url;
//    private String login;
//    private String password;
}
