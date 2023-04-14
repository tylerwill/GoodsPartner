package com.goodspartner.web.controller;

import com.goodspartner.service.security.SecurityService;
import com.goodspartner.web.controller.request.AuthRequest;
import com.goodspartner.web.controller.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    private final SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        try {
            return ResponseEntity.ok(securityService.authenticate(authRequest));
        } catch (BadCredentialsException e) {
            log.info(String.format("Login %s. Bad credentials", authRequest.getUsername()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> getNewAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.of(securityService.getAccessToken(token));
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> getNewRefreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.of(securityService.getRefreshToken(token));
    }


    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }
}
