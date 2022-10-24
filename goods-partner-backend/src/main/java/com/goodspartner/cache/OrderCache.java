package com.goodspartner.cache;

import com.goodspartner.dto.OrderDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@NoArgsConstructor
public class OrderCache {

    private final Map<UUID, List<OrderDto>> cacheDataMap = new ConcurrentHashMap<>();

    public Optional<List<OrderDto>> getOrders(UUID deliveryId) {
        if (cacheDataMap.containsKey(deliveryId)) {
            return Optional.of(cacheDataMap.get(deliveryId));
        }
        return Optional.empty();
    }

    public void saveOrders(UUID deliveryId, List<OrderDto> orders) {
        cacheDataMap.put(deliveryId, orders);
    }

    public List<OrderDto> removeOrders(UUID deliveryId) {
        return cacheDataMap.remove(deliveryId);
    }
}