package com.goodspartner.config;

import com.goodspartner.mapper.OrderMapper;
import com.goodspartner.util.DtoCalculationHelper;
import com.goodspartner.repository.OrderRepository;
import com.goodspartner.service.OrderService;
import com.goodspartner.service.impl.DefaultOrderService;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestContextConfiguration {

    @Bean
    @Primary
    public OrderService getDefaultImpl(OrderRepository orderRepository,
                                       OrderMapper orderMapper, DtoCalculationHelper dtoHelper) {
        return new DefaultOrderService(orderRepository, orderMapper, dtoHelper);
    }

}
