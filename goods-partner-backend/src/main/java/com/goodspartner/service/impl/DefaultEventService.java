package com.goodspartner.service.impl;

import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressExternal.OrderAddressId;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.User;
import com.goodspartner.event.Action;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
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

import static com.goodspartner.entity.DeliveryHistoryTemplate.DELIVERY_CALCULATED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.DELIVERY_COMPLETED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_POINT_STATUS;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_START;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_STATUS;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_STATUS_AUTO;

@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private final LiveEventService liveEventService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID deliverId) {
        String action = fillActionWithAuditor(template.getTemplate());

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(action, deliverId);

        applicationEventPublisher.publishEvent(deliveryAuditEvent);

        EventType type = EventType.INFO;
        Action eventAction = new Action(ActionType.DELIVERY_UPDATED, deliverId);

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
        List<DeliveryHistoryTemplate> templates = List.of(ROUTE_POINT_STATUS, ROUTE_STATUS, ROUTE_STATUS_AUTO);

        StringSubstitutor sub = new StringSubstitutor(values);

        String action = sub.replace(template.getTemplate());

        UUID deliveryId = route.getDelivery().getId();

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(fillActionWithAuditor(action), deliveryId);

        User user = userService.findByRouteId(route.getId());

        EventType type = EventType.INFO;
        Action eventAction = null;

        if (templates.contains(template)) {
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
