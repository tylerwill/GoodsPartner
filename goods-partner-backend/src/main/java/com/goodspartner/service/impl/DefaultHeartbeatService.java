package com.goodspartner.service.impl;

import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.service.HeartbeatService;
import com.goodspartner.service.LiveEventService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@EnableScheduling
public class DefaultHeartbeatService implements HeartbeatService {

    private static final int SSE_HEART_BEAT_FIXED_RATE = 20000;

    private final LiveEventService eventService;

    @Override
    @Scheduled(fixedRate = SSE_HEART_BEAT_FIXED_RATE)
    public void pushBeat() {
        eventService.publish(new LiveEvent("Keep alive", EventType.HEARTBEAT));
    }
}
