package com.goodspartner.web.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private int id;
    private String userName;
    private String email;
    private String role;
    private boolean enabled = false;
    private UUID heartbeatId;
}
