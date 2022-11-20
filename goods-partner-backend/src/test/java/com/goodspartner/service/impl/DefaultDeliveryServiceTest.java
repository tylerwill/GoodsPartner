package com.goodspartner.service.impl;

import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractBaseITest;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DBRider
@Disabled
// TODO rework after moving RoutePoints to separate table
class DefaultDeliveryServiceTest extends AbstractBaseITest {

    @Autowired
    private DeliveryService deliveryService;

    @Test
    @DisplayName("when Calculate Delivery then delivery fetched by id, orders loaded and analyzed for excluded parameter")
    void testCalculateDelivery_WithExcludedParameter() {

        //add mocks to construct mock Delivery object
        DeliveryRepository deliveryRepositoryMock = mock(DeliveryRepository.class);
        DeliveryMapper deliveryMapperMock = mock(DeliveryMapper.class);
        CarRepository carRepositoryMock = mock(CarRepository.class);
        UserService userServiceMock = mock(UserService.class);

        //construct mock Delivery object
        DeliveryService mockService =
                new DefaultDeliveryService(deliveryMapperMock, deliveryRepositoryMock, carRepositoryMock, userServiceMock);

        //configure mock objects
        Delivery mockDelivery = mock(Delivery.class);
        List<OrderExternal> includedListMock = mock(List.class);

        when(deliveryRepositoryMock.findById(any(UUID.class))).thenReturn(Optional.ofNullable(mockDelivery));
        when(Objects.requireNonNull(mockDelivery).getStatus()).thenReturn(DeliveryStatus.DRAFT);
        when(mockDelivery.getOrders()).thenReturn(includedListMock);
        when(includedListMock.isEmpty()).thenReturn(false);

        //call tested method
//        mockService.calculateDelivery(UUID.fromString("11111111-2222-2222-2222-333333333333"));

        //verify invocation
//        verify(deliveryCalculationHelper).calculateDelivery(mockDelivery);
    }
}