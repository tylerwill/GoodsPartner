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
@ConfigurationProperties(prefix = "client.accounting")
public class ClientProperties {

    //    private String login;
    //    private String password;

    private String clientServerURL; // Apache path

    private String server1CUriPrefix; // 1C base uri

    private String documentsUriPrefix;

}
