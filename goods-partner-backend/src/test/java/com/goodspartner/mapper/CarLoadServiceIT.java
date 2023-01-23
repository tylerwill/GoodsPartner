package com.goodspartner.mapper;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.repository.RouteRepository;
import com.goodspartner.service.CarLoadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DBRider
class CarLoadServiceIT extends AbstractBaseITest {

    private final static UUID DELIVERY_ID = UUID.fromString("49228d27-2ce7-4246-b7c3-e53c143e5550");

    @Autowired
    private CarLoadService carLoadService;

    @Autowired
    private OrderExternalRepository orderExternalRepository;

    @Autowired
    private RouteRepository routeRepository;

    // This is needed to overcome the LazyTransaction with exisitng repo methods, otherwise we fal into multiple bag exception
    @Transactional
    @Test
    @DataSet(value = {"datasets/route-points/common-dataset.json",
            "datasets/route-points/route-point-complete-dataset.json"},
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void testMappingRoutesToCarloads() {
        // given
        List<OrderExternal> orders = orderExternalRepository.findByDeliveryId(DELIVERY_ID, Sort.unsorted());
        List<Route> routes = routeRepository.findByDeliveryIdExtended(DELIVERY_ID, Sort.unsorted());
        //when
        List<CarLoad> carLoads = carLoadService.buildCarLoad(routes, orders);
        //then
        assertEquals(1, carLoads.size());
        CarLoad carLoad = carLoads.get(0);
        assertEquals(orders.size(), carLoad.getOrders().size());
    }
}