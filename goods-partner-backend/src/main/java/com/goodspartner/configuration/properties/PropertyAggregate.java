package com.goodspartner.configuration.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PropertyAggregate {

    private ClientRoutingProperties clientRoutingProperties;

    private ClientBusinessProperties clientBusinessProperties;

}
