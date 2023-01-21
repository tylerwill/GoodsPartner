package com.goodspartner.service.impl;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.event.DeliveryAuditEvent;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.repository.DeliveryHistoryRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.util.AuditorBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultDeliveryHistoryService implements DeliveryHistoryService {

    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryHistory> findByDeliveryId(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
        return deliveryHistoryRepository.findByDeliveryOrderByCreatedAtDesc(delivery);
    }

    @EventListener(DeliveryAuditEvent.class)
    public void handleEvent(DeliveryAuditEvent event) {
        deliveryHistoryRepository.save(createNewDeliveryHistory(event));
    }

    private DeliveryHistory createNewDeliveryHistory(DeliveryAuditEvent event) {
        Delivery delivery = deliveryRepository.findById(event.getDeliveryId())
                .orElseThrow(() -> new DeliveryNotFoundException(event.getDeliveryId()));

        Map<String, String> currentAuditorData = AuditorBuilder.getCurrentAuditorData();
        String role = currentAuditorData.get("roleTranslated");
        String userEmail = currentAuditorData.get("userEmail");

        return DeliveryHistory.builder()
                .delivery(delivery)
                .createdAt(LocalDateTime.now())
                .role(role)
                .userEmail(userEmail)
                .action(event.getAction())
                .build();
    }
}
