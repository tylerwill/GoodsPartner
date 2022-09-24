package com.goodspartner.service.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.exceptions.UnknownAddressException;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.OrderExternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.goodspartner.dto.MapPoint.AddressStatus.UNKNOWN;
import static java.lang.String.format;

@RequiredArgsConstructor
@Slf4j
@Service
public class DefaultOrderExternalService implements OrderExternalService {

    // Exceptions
    public static final String DELIVERY_NOT_FOUND = "Cannot find delivery with id: %s";

    private final OrderExternalMapper orderExternalMapper;
    private final OrderExternalRepository orderExternalRepository;

    private final AddressExternalRepository addressExternalRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional
    public void saveValidOrdersAndEnrichKnownAddressesCache(UUID deliveryId, List<OrderDto> orderDtos) {

        validateOrderAddresses(orderDtos);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(format(DELIVERY_NOT_FOUND, deliveryId)));

        orderDtos.forEach(orderDto -> orderDto.setDeliveryId(deliveryId));

        List<OrderExternal> externalOrders = orderExternalMapper.mapOrderDtosToOrdersExternal(orderDtos);
        externalOrders.forEach(externalOrder -> externalOrder.setDelivery(delivery));
        orderExternalRepository.saveAll(externalOrders);
    }

    private void validateOrderAddresses(List<OrderDto> orderDtos) {
        orderDtos
                .stream()
                .map(OrderDto::getMapPoint)
                .filter(mapPoint -> UNKNOWN.equals(mapPoint.getStatus()))
                .findFirst()
                .ifPresent(mapPoint -> {
                    throw new UnknownAddressException("address " + mapPoint.getAddress() + " is in Unknown status");
                });
    }
}