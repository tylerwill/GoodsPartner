package com.goodspartner.web.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private User user;

    @Builder
    @Data
    public static class User {
        private String username;
        private String role;
        private boolean enabled = false;
        private UUID heartbeatId;
    }
}
