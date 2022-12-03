package com.goodspartner.service;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface HeartbeatService {

    void pushBeat(OAuth2AuthenticationToken authentication);
}
