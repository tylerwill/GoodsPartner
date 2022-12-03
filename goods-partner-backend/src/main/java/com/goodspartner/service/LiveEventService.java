package com.goodspartner.service;

import com.goodspartner.entity.User;
import com.goodspartner.event.LiveEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.function.Consumer;

public interface LiveEventService {

    void subscribe(OAuth2AuthenticationToken authentication, Consumer<LiveEvent> event);

    void publishHeartBeat(OAuth2AuthenticationToken authentication, LiveEvent event);

    void publishToDriver(LiveEvent event, User driver);

    void publishToAdminAndLogistician(LiveEvent event);
}
