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
    public List<DeliveryHistoryDto> findByDeliveryId(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
        return deliveryHistoryMapper.toDeliveryHistoryDtos(
                deliveryHistoryRepository.findByDeliveryOrderByCreatedAtDesc(delivery));
    }

    @Override
    public void publish(DeliveryAuditEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
