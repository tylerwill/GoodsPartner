package com.goodspartner.service.impl;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.event.ActionType;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.event.Action;
import com.goodspartner.event.EventType;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.service.EventService;
import com.goodspartner.service.LiveEventService;
import com.goodspartner.util.AuditorBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryHistoryTemplate.DELIVERY_CALCULATED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.DELIVERY_COMPLETED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.DELIVERY_UPDATED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_POINT_STATUS;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_START;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_STATUS;
import static com.goodspartner.entity.DeliveryHistoryTemplate.ROUTE_STATUS_AUTO;

@Service
@RequiredArgsConstructor
public class DefaultEventService implements EventService {

    private final DeliveryHistoryService deliveryHistoryService;
    private final LiveEventService liveEventService;

    @Override
    public void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID deliverId) {
        String action = fillActionWithAuditor(template.getTemplate());

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(action, deliverId);

        deliveryHistoryService.publish(deliveryAuditEvent);

        EventType type = EventType.INFO;
        Action eventAction = null;

        if (template.equals(DELIVERY_CALCULATED)) {
            type = EventType.SUCCESS;
            eventAction = new Action(ActionType.DELIVERY_UPDATED, deliverId);
        }

        if (template.equals(DELIVERY_UPDATED)) {
            eventAction = new Action(ActionType.DELIVERY_UPDATED, deliverId);
        }
        liveEventService.publish(new LiveEvent(deliveryAuditEvent.getName(), type, eventAction));
    }

    @Override
    public void publishDeliveryCompleted(Delivery delivery) {
        DeliveryHistoryTemplate template = DELIVERY_COMPLETED;
        String action = template.getTemplate();

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(action, delivery.getId());

        deliveryHistoryService.publish(deliveryAuditEvent);
        liveEventService.publish(new LiveEvent(deliveryAuditEvent.getName(), EventType.INFO));
    }

    @Override
    public void publishRouteUpdated(Route updateRoute) {
        if (updateRoute.getStatus().equals(RouteStatus.INPROGRESS)) {
            DeliveryHistoryTemplate template = ROUTE_START;

            Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
            values.put("carLicensePlate", updateRoute.getCar().getLicencePlate());
            values.put("routeStatus", updateRoute.getStatus().toString());
            values.put("carName", updateRoute.getCar().getName());

            publishPreparedEvent(values, template, updateRoute.getDelivery().getId());

        } else {
            DeliveryHistoryTemplate template = ROUTE_STATUS;

            Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
            values.put("carName", updateRoute.getCar().getName());
            values.put("carLicensePlate", updateRoute.getCar().getLicencePlate());
            values.put("routeStatus", updateRoute.getStatus().toString());

            publishPreparedEvent(values, template, updateRoute.getDelivery().getId());
        }

    }

    @Override
    public void publishRoutePointUpdated(RoutePoint routePoint, Route route) {
        DeliveryHistoryTemplate template = ROUTE_POINT_STATUS;

        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("clientName", routePoint.getClientName());
        values.put("clientAddress", routePoint.getAddress());
        values.put("routePointStatus", routePoint.getStatus().toString());

        publishPreparedEvent(values, template, route.getDelivery().getId());
    }

    @Override
    public void publishOrdersStatus(DeliveryHistoryTemplate template, UUID deliveryId) {
        String message = fillActionWithAuditor(template.getTemplate());

        EventType type = EventType.INFO;
        Action eventAction = null;

        if (template.equals(ORDERS_LOADED)) {
            type = EventType.SUCCESS;
            eventAction = new Action(ActionType.DELIVERY_UPDATED, deliveryId);
        }

        liveEventService.publish(new LiveEvent(message, type, eventAction));

    }

    @Override
    public void publishRouteStatusChangeAuto(Route route) {
        Map<String, String> values = new HashMap<>();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("routeStatus", route.getStatus().getStatus());

        publishPreparedEvent(values, ROUTE_STATUS_AUTO, route.getDelivery().getId());
    }

    private void publishPreparedEvent(Map<String, String> values, DeliveryHistoryTemplate template, UUID deliveryId) {
        List<DeliveryHistoryTemplate> templates = List.of(ROUTE_POINT_STATUS, ROUTE_STATUS, ROUTE_STATUS_AUTO);

        StringSubstitutor sub = new StringSubstitutor(values);

        String action = sub.replace(template.getTemplate());

        DeliveryAuditEvent deliveryAuditEvent = new DeliveryAuditEvent(fillActionWithAuditor(action), deliveryId);

        EventType type = EventType.INFO;
        Action eventAction = null;

        if (templates.contains(template)) {
            eventAction = new Action(ActionType.DELIVERY_UPDATED, deliveryId);
        }

        deliveryHistoryService.publish(deliveryAuditEvent);
        liveEventService.publish(new LiveEvent(deliveryAuditEvent.getName(), type, eventAction));
    }

    private String fillActionWithAuditor(String template) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        return StringSubstitutor.replace(template, values, "{", "}");
    }
}
