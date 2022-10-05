package com.goodspartner.cache;

import com.goodspartner.dto.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class CarLocationCache {
    private Map<Integer, Location> cacheDataMap = new ConcurrentHashMap<>();

    public Location getLocation(int id) {
        if (cacheDataMap.containsKey(id)) {
            return cacheDataMap.get(id);
        }
        return new Location();
    }

    public void saveLocation(int id, Location location) {
        cacheDataMap.put(id, location);
    }
}
