package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryHistoryDto;
import com.goodspartner.dto.RouteDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.mapper.DeliveryHistoryMapper;
import com.goodspartner.repository.DeliveryHistoryRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.util.AuditorBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return deliveryHistoryMapper.toDeliveryHistoryDtos(deliveryHistoryRepository.findByDelivery(delivery));
    }

    @Override
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
    public void publishIfRouteUpdated(RouteDto routeDto, RouteStatus oldRouteStatus, Route updateRoute) {
        if (!routeDto.getStatus().equals(oldRouteStatus)) {
            if (routeDto.getStatus().equals(RouteStatus.INPROGRESS)) {
                publishRouteStart(updateRoute, routeDto);
            } else {
                publishRouteStatusUpdated(updateRoute, routeDto);
            }
        }
    }

    @Override
    public void publishIfPointUpdated(RoutePoint routePoint, RoutePointStatus oldRoutePointStatus, Route route) {
        if (!routePoint.getStatus().equals(oldRoutePointStatus)) {
            String template = DeliveryHistoryTemplate.ROUTE_POINT_STATUS.getTemplate();

            Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
            values.put("carName", route.getCar().getName());
            values.put("carLicensePlate", route.getCar().getLicencePlate());
            values.put("clientName", routePoint.getClientName());
            values.put("clientAddress", routePoint.getAddress());
            values.put("routePointStatus", routePoint.getStatus().toString());

            publishPreparedEvent(values, template, route.getDelivery().getId());
        }
    }

    @Override
    public void publishRouteStatusChangeAuto(RouteStatus routeStatus, Route route) {
        String template = DeliveryHistoryTemplate.ROUTE_STATUS_AUTO.getTemplate();

        Map<String, String> values = new HashMap<>();
        values.put("carName", route.getCar().getName());
        values.put("carLicensePlate", route.getCar().getLicencePlate());
        values.put("routeStatus", routeStatus.toString());

        publishPreparedEvent(values, template, route.getDelivery().getId());
    }

    private void publishRouteStart(Route updateRoute, RouteDto routeDto) {
        String template = DeliveryHistoryTemplate.ROUTE_START.getTemplate();

        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        values.put("carLicensePlate", routeDto.getCar().getLicencePlate());
        values.put("routeStatus", routeDto.getStatus().toString());
        values.put("carName", routeDto.getCar().getName());

        publishPreparedEvent(values, template, updateRoute.getDelivery().getId());
    }

    private void publishRouteStatusUpdated(Route updateRoute, RouteDto routeDto) {
        String template = DeliveryHistoryTemplate.ROUTE_STATUS.getTemplate();

        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        values.put("carName", routeDto.getCar().getName());
        values.put("carLicensePlate", routeDto.getCar().getLicencePlate());
        values.put("routeStatus", routeDto.getStatus().toString());

        publishPreparedEvent(values, template, updateRoute.getDelivery().getId());
    }

    private void publishPreparedEvent(Map<String, String> values, String template, UUID deliveryId) {
        StringSubstitutor sub = new StringSubstitutor(values);
        String action = sub.replace(template);
        applicationEventPublisher.publishEvent(new DeliveryAuditEvent(fillActionWithAuditor(action), deliveryId));
    }

    private String fillActionWithAuditor(String template) {
        Map<String, String> values = AuditorBuilder.getCurrentAuditorData();
        return StringSubstitutor.replace(template, values, "{", "}");
    }
}
