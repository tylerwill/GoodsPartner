package com.goodspartner.service;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;

import java.util.List;

public interface GeocodeService {

    void enrichValidAddressForRegularOrders(List<OrderDto> orders);

    void validateOurOfRegion(MapPoint mapPoint);

}