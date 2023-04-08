package com.goodspartner.service.security;

import com.goodspartner.dto.AuthenticationRequestDto;
import com.goodspartner.dto.AuthenticationResponseDto;

public interface SecurityService {
    AuthenticationResponseDto authenticate(AuthenticationRequestDto authRequest);

}
