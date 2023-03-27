package com.goodspartner.facade.impl;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.EventType;
import com.goodspartner.exception.UnknownAddressException;
import com.goodspartner.facade.OrderFacade;
import com.goodspartner.mapper.OrderExternalMapper;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.GeocodeService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.OrderExternalService;
import com.goodspartner.service.util.ExternalOrderPostProcessor;
import com.goodspartner.web.controller.request.ExcludeOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.goodspartner.event.EventMessageTemplate.EventPlaceholder.DELIVERY_DATE;
import static com.goodspartner.event.EventMessageTemplate.EventPlaceholder.LOADED_ORDERS_VALUE;
import static com.goodspartner.event.EventMessageTemplate.ORDERS_LOADED;
import static com.goodspartner.event.EventMessageTemplate.ORDERS_LOADING;
import static com.goodspartner.event.EventMessageTemplate.ORDERS_LOADING_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultOrderFacade implements OrderFacade {
    // Services
    private final OrderExternalService orderExternalService;
    private final DeliveryService deliveryService;
    private final EventService eventService;
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

        log.info("Fetching orders from client CRM by date: {}", deliveryDate);
        try {
            String ordersLoadingMessage = ORDERS_LOADING.withValues(DELIVERY_DATE, delivery.getDeliveryDate().format(DateTimeFormatter.ISO_DATE));
            eventService.publishForLogist(ordersLoadingMessage, EventType.INFO, ActionType.INFO, deliveryId);

            List<OrderDto> orderDtos = integrationService.findAllByShippingDate(deliveryDate);
            orderCommentProcessor.processOrderComments(orderDtos);
            geocodeService.enrichValidAddressForRegularOrders(orderDtos);

            List<OrderExternal> externalOrders = orderExternalMapper.toOrderExternalList(orderDtos);
            orderExternalService.bindExternalOrdersWithDelivery(externalOrders, delivery);

            String ordersLoadedMessage = ORDERS_LOADED.withValues(LOADED_ORDERS_VALUE, String.valueOf(externalOrders.size()));
            eventService.publishForLogist(ordersLoadedMessage, EventType.SUCCESS, ActionType.ORDER_UPDATED, deliveryId);
            log.info("Saved orders for delivery {} on {} deliveryDate", deliveryId, deliveryDate);

        } catch (Exception exception) {
            // Update delivery status
            delivery.setFormationStatus(DeliveryFormationStatus.ORDERS_LOADING_FAILED);
            deliveryService.update(delivery);
            // Notify failed
            eventService.publishForLogist(ORDERS_LOADING_FAILED.getTemplate(), EventType.ERROR, ActionType.DELIVERY_UPDATED, deliveryId);
            log.error("Failed retrieve orders for delivery by date: {}", deliveryDate, exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public OrderExternal update(long id, OrderDto orderDto) {
        log.info("Updating order with id: {}", id);
        geocodeService.validateOutOfRegion(orderDto);

        OrderExternal updatedOrder = orderExternalService.update(id, orderDto);

        // Should be executed under separate transaction to fetch state after update
        orderExternalService.checkDeliveryReadiness(updatedOrder.getDelivery());

        return updatedOrder;
    }

    // TODO do we need to check delivery status == DRAFT here
    @Override
    public OrderExternal excludeOrder(long id, ExcludeOrderRequest excludeOrderRequest) {
        log.info("Excluding order with id: {}", id);

        OrderExternal excludedOrder = orderExternalService.excludeOrder(id, excludeOrderRequest);

        orderExternalService.checkDeliveryReadiness(excludedOrder.getDelivery()); // Should be executed under separate transaction to fetch state after update

        return excludedOrder;
    }

    @Override
    public void validateOrdersForDeliveryCalculation(UUID deliveryId) {
        orderExternalService.getInvalidOrdersForCalculation(deliveryId)
                .stream()
                .findFirst()
                .ifPresent(orderExternal -> {
                    throw new UnknownAddressException(orderExternal);
                });
    }
}
