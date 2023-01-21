package com.goodspartner.service.impl;

import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.service.HeartbeatService;
import com.goodspartner.service.LiveEventService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DefaultHeartbeatService implements HeartbeatService {

    private static final LiveEvent HEAR_BEAT_EVENT = new LiveEvent("Keep alive", EventType.HEARTBEAT);

    private final LiveEventService eventService;

    @Override
    public void pushBeat(OAuth2AuthenticationToken authenticationToken) {
        eventService.publishHeartBeat(authenticationToken, HEAR_BEAT_EVENT);
    }
}
