package com.goodspartner.service.google;

import com.goodspartner.AbstractBaseITest;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.exception.GoogleApiException;
import com.goodspartner.service.client.GoogleClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

class GoogleGeocodeServiceIT extends AbstractBaseITest {

    private final String invalidAddress = "";
    private final String clientName = "validClientName";

    @Autowired
    private GoogleGeocodeService googleGeocodeService;

    @MockBean
    private GoogleClient googleClient;

    @Test
    void whenGoogleClientThrowsException_UnknownMapPointReturned() {
        // given
        Mockito.when(googleClient.getGeocodingResults(invalidAddress)).thenThrow(new GoogleApiException());
        OrderDto orderWithInvalidAddress = getOrderWithInvalidAddress();
        List<OrderDto> orderDtos = Collections.singletonList(orderWithInvalidAddress);
        // when
        googleGeocodeService.enrichValidAddressForRegularOrders(orderDtos);
        // then
        MapPoint mapPoint = orderWithInvalidAddress.getMapPoint();
        Assertions.assertEquals(AddressStatus.UNKNOWN, mapPoint.getStatus());
    }

    private OrderDto getOrderWithInvalidAddress() {
        return OrderDto.builder()
                .deliveryType(DeliveryType.REGULAR)
                .address(invalidAddress)
                .clientName(clientName)
                .build();
    }
}