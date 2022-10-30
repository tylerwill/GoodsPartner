package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.mapper.DeliveryHistoryMapper;
import com.goodspartner.repository.DeliveryHistoryRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultDeliveryHistoryService implements DeliveryHistoryService {

    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final DeliveryHistoryMapper deliveryHistoryMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional
    public void add(DeliveryHistory deliveryHistory) {
        deliveryHistoryRepository.save(deliveryHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryHistoryDto> findByDelivery(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
        return deliveryHistoryMapper.toDeliveryHistoryDtos(
                deliveryHistoryRepository.findByDeliveryOrderByCreatedAtDesc(delivery));
    }

    @Override
    public void publish(DeliveryAuditEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    /*@Override
    public void publishDeliveryEvent(DeliveryHistoryTemplate template, UUID deliverId) {
        String action = fillActionWithAuditor(template.getTemplate());
        applicationEventPublisher.publishEvent(new DeliveryAuditEvent(action, deliverId));
    }

    @Override
    public void publishDeliveryCompleted(Delivery delivery) {
        String action = DeliveryHistoryTemplate.DELIVERY_COMPLETED.getTemplate();
        applicationEventPublisher.publishEvent(new DeliveryAuditEvent(action, delivery.getId()));
    }

    @Override
    public void publishRouteUpdated(Route updateRoute) {
        if (updateRoute.getStatus().equals(RouteStatus.INPROGRESS)) {
            String template = DeliveryHistoryTemplate.ROUTE_START.getTemplate();

            Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
            values.put("carLicensePlate", updateRoute.getCar().getLicencePlate());
            values.put("routeStatus", updateRoute.getStatus().toString());
            values.put("carName", updateRoute.getCar().getName());

            publishPreparedEvent(values, template, updateRoute.getDelivery().getId());

        } else {

            String template = DeliveryHistoryTemplate.ROUTE_STATUS.getTemplate();

            Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
            values.put("carName", updateRoute.getCar().getName());
            values.put("carLicensePlate", updateRoute.getCar().getLicencePlate());
            values.put("routeStatus", updateRoute.getStatus().toString());

            publishPreparedEvent(values, template, updateRoute.getDelivery().getId());
        }

    }

    @Override
    public void publishRoutePointUpdated(RoutePoint routePoint, Route route) {
        String template = DeliveryHistoryTemplate.ROUTE_POINT_STATUS.getTemplate();

        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("clientName", routePoint.getClientName());
        values.put("clientAddress", routePoint.getAddress());
        values.put("routePointStatus", routePoint.getStatus().toString());

        publishPreparedEvent(values, template, route.getDelivery().getId());
    }

    @Override
    public void publishRouteStatusChangeAuto(Route route) {
        String template = DeliveryHistoryTemplate.ROUTE_STATUS_AUTO.getTemplate();

        Map<String, String> values = new HashMap<>();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("routeStatus", route.getStatus().getStatus());

        publishPreparedEvent(values, template, route.getDelivery().getId());
    }

    private void publishPreparedEvent(Map<String, String> values, String template, UUID deliveryId) {
        StringSubstitutor sub = new StringSubstitutor(values);
        String action = sub.replace(template);
        applicationEventPublisher.publishEvent(new DeliveryAuditEvent(fillActionWithAuditor(action), deliveryId));
    }

    private String fillActionWithAuditor(String template) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        return StringSubstitutor.replace(template, values, "{", "}");
    }*/
}
