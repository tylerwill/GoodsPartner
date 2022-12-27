package com.goodspartner.facade.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.event.Action;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.facade.OrderFacade;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.GeocodeService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.LiveEventService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.util.ExternalOrderPostProcessor;
import com.goodspartner.web.controller.request.ExcludeOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryHistoryTemplate.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ORDERS_LOADING;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultOrderFacade implements OrderFacade {
    // Services
    private final DeliveryService deliveryService;
    private final OrderExternalService orderExternalService;
    private final EventService eventService;
    private final LiveEventService liveEventService;
    private final GeocodeService geocodeService;
    private final IntegrationService integrationService; // GrangeDolceIntegration
    // Utils
    private final ExternalOrderPostProcessor orderCommentProcessor;
    // Mappers
    private final OrderExternalMapper orderExternalMapper;

    @Async("goodsPartnerThreadPoolTaskExecutor")
    @Override
    public void processOrdersForDeliveryAsync(Delivery delivery) {

        UUID deliveryId = delivery.getId();
        LocalDate deliveryDate = delivery.getDeliveryDate();

        log.info("Fetching orders from 1C");
        try {
            eventService.publishOrdersStatus(ORDERS_LOADING, deliveryId);

            List<OrderDto> orderDtos = integrationService.findAllByShippingDate(deliveryDate);
            orderCommentProcessor.processOrderComments(orderDtos);
            geocodeService.enrichValidAddressForRegularOrders(orderDtos);

            List<OrderExternal> externalOrders = orderExternalMapper.toOrderExternalList(orderDtos);
            orderExternalService.bindExternalOrdersWithDelivery(externalOrders, delivery);

            eventService.publishOrdersStatus(ORDERS_LOADED, deliveryId);
            log.info("Saved orders for delivery {} on {} deliveryDate", deliveryId, deliveryDate);

        } catch (Exception exception) {
            eventService.publishEvent(new LiveEvent("Помилка під час вивантаження замовлень з 1С",
                    EventType.ERROR, new Action(ActionType.INFO, deliveryId)));
            log.error("Failed to save orders to cache for delivery by date: {}", deliveryDate, exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public OrderExternal update(long id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);
        geocodeService.validateOutOfRegion(orderDto);

        Delivery delivery = deliveryService.findById(orderDto.getDeliveryId());
        if (!DeliveryStatus.DRAFT.equals(delivery.getStatus())) {
            throw new IllegalDeliveryStatusForOperation(delivery, "update order for");
        }

        OrderExternal updatedOrder = orderExternalService.update(id, orderDto);

        // Should be executed under separate transaction to fetch state after update
        orderExternalService.checkDeliveryReadiness(delivery);

        return updatedOrder;
    }

    // TODO do we need to check delivery status == DRAFT here
    @Override
    public OrderExternal excludeOrder(long id, ExcludeOrderRequest excludeOrderRequest) {
        log.info("Excluding order with id: {}", id);

        OrderExternal orderExternal = orderExternalService.excludeOrder(id, excludeOrderRequest);

        orderExternalService.checkDeliveryReadiness(orderExternal.getDelivery()); // Should be executed under separate transaction to fetch state after update

        return orderExternal;
    }
}
