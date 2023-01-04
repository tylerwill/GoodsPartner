package com.goodspartner.service.impl;

import com.goodspartner.entity.*;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.event.*;
import com.goodspartner.service.EventService;
import com.goodspartner.service.LiveEventService;
import com.goodspartner.service.UserService;
import com.goodspartner.util.AuditorBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryHistoryTemplate.*;

@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private static final List<DeliveryHistoryTemplate> ROUTE_IMPACT_TEMPLATES =
            List.of(ROUTE_POINT_STATUS, ROUTE_STATUS, ROUTE_STATUS_AUTO, ROUTE_POINT_TIME_RANGE_WARNING);

    private final LiveEventService liveEventService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID deliveryId) {
        String action = fillActionWithAuditor(template.getTemplate());

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(action, deliveryId);

        applicationEventPublisher.publishEvent(deliveryAuditEvent);

        EventType type = EventType.INFO;

        ActionType actionType = template == DELIVERY_CREATED ? ActionType.DELIVERY_CREATED : ActionType.DELIVERY_UPDATED;

        Action eventAction = new Action(actionType, deliveryId);

        if (template.equals(DELIVERY_CALCULATED)) {
            type = EventType.SUCCESS;
        }

        liveEventService.publishToAdminAndLogistician(new LiveEvent(deliveryAuditEvent.getAction(), type, eventAction));
    }

    @Override
    public void publishDeliveryCompleted(Delivery delivery) {
        String action = DELIVERY_COMPLETED.getTemplate();
        UUID deliveryId = delivery.getId();

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(action, deliveryId);

        applicationEventPublisher.publishEvent(deliveryAuditEvent);
        liveEventService.publishToAdminAndLogistician(new LiveEvent(deliveryAuditEvent.getAction(), EventType.INFO, new Action(ActionType.INFO, deliveryId)));
    }

    @Override
    public void publishRouteUpdated(Route updateRoute) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();

        if (updateRoute.getStatus().equals(RouteStatus.INPROGRESS)) {
            values.put("carLicensePlate", updateRoute.getCar().getLicencePlate());
            values.put("routeStatus", updateRoute.getStatus().toString());
            values.put("carName", updateRoute.getCar().getName());

            publishPreparedEvent(values, ROUTE_START, updateRoute);
        } else {
            values.put("carName", updateRoute.getCar().getName());
            values.put("carLicensePlate", updateRoute.getCar().getLicencePlate());
            values.put("routeStatus", updateRoute.getStatus().toString());

            publishPreparedEvent(values, ROUTE_STATUS, updateRoute);
        }
    }

    @Override
    public void publishRoutePointUpdated(RoutePoint routePoint, Route route) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("routePointStatus", routePoint.getStatus().toString());

        AddressExternal addressExternal = routePoint.getAddressExternal();
        OrderAddressId orderAddressId = addressExternal.getOrderAddressId();
        values.put("clientName", orderAddressId.getClientName());
        values.put("clientAddress", orderAddressId.getOrderAddress());

        publishPreparedEvent(values, ROUTE_POINT_STATUS, route);
    }

    @Override
    public void publishDeliveryTimeRangeWarning(Route route) {
        Map<String, String> values = new HashMap<>();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        publishPreparedEvent(values, ROUTE_POINT_TIME_RANGE_WARNING, route);
    }

    @Override
    public void publishCoordinatesUpdated(RoutePoint routePoint, AddressExternal addressExternal) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();

        Route route = routePoint.getRoute();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("routePointStatus", routePoint.getStatus().toString());

        OrderAddressId orderAddressId = addressExternal.getOrderAddressId();
        values.put("clientName", orderAddressId.getClientName());
        values.put("clientAddress", orderAddressId.getOrderAddress());

        publishPreparedEvent(values, DRIVER_CLIENT_ADDRESS_UPDATE, route);
    }

    @Override
    public void publishOrdersStatus(DeliveryHistoryTemplate template, UUID deliveryId) {
        String message = fillActionWithAuditor(template.getTemplate());

        if (template.equals(ORDERS_LOADED)) {
            liveEventService.publishToAdminAndLogistician(
                    new LiveEvent(message, EventType.SUCCESS, new Action(ActionType.ORDER_UPDATED, deliveryId)));
        } else {
            liveEventService.publishToAdminAndLogistician(
                    new LiveEvent(message, EventType.INFO, new Action(ActionType.INFO, deliveryId)));
        }
    }

    @Override
    public void publishEvent(LiveEvent event) {
        liveEventService.publishToAdminAndLogistician(event);
    }

    @Override
    public void publishRouteStatusChangeAuto(Route route) {
        Map<String, String> values = new HashMap<>();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("routeStatus", route.getStatus().getStatus());

        publishPreparedEvent(values, ROUTE_STATUS_AUTO, route);
    }

    private void publishPreparedEvent(Map<String, String> values, DeliveryHistoryTemplate template, Route route) {

        StringSubstitutor sub = new StringSubstitutor(values);

        String action = sub.replace(template.getTemplate());

        UUID deliveryId = route.getDelivery().getId();

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(fillActionWithAuditor(action), deliveryId);

        User user = userService.findByRouteId(route.getId());

        EventType type = EventType.INFO;

        Action eventAction;
        if (ROUTE_IMPACT_TEMPLATES.contains(template)) {
            eventAction = new Action(ActionType.ROUTE_UPDATED, deliveryId);
        } else {
            eventAction = new Action(ActionType.INFO, deliveryId);
        }

        applicationEventPublisher.publishEvent(deliveryAuditEvent);

        liveEventService.publishToDriver(new LiveEvent(deliveryAuditEvent.getAction(), type, eventAction), user);
    }

    private String fillActionWithAuditor(String template) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        return StringSubstitutor.replace(template, values);
    }
}
