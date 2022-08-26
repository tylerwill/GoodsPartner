package com.goodspartner.config;

import com.goodspartner.mapper.OrderMapper;
import com.goodspartner.util.DtoCalculationHelper;
import com.goodspartner.repository.OrderRepository;
import com.goodspartner.service.OrderService;
import com.goodspartner.service.impl.DefaultOrderService;
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
