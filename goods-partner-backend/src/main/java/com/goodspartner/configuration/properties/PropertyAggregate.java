package com.goodspartner.configuration.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PropertyAggregate {

    private ClientProperties clientProperties;
    private ClientBusinessProperties clientBusinessProperties;
    private GoogleGeocodeProperties googleGeocodeProperties;

}
