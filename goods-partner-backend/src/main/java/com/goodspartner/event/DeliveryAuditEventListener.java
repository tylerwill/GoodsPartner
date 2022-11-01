package com.goodspartner.event;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryHistory;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryHistoryService;
import com.goodspartner.util.AuditorBuilder;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@AllArgsConstructor
public class DeliveryAuditEventListener {

    private final DeliveryHistoryService deliveryHistoryService;
    private final DeliveryRepository deliveryRepository;

    @EventListener(DeliveryAuditEvent.class)
    public void handleEvent(DeliveryAuditEvent event){
        deliveryHistoryService.add(createNewDeliveryHistory(event));
    }

    private DeliveryHistory createNewDeliveryHistory(DeliveryAuditEvent event) {
        Delivery delivery = deliveryRepository.findById(event.getDeliveryId())
                .orElseThrow(() -> new DeliveryNotFoundException(event.getDeliveryId()));

        Map<String, String> currentAuditorData = AuditorBuilder.getCurrentAuditorData();
        String role = currentAuditorData.get("role");
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
