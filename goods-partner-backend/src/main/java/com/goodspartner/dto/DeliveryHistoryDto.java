package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHistoryDto {
    private UUID id;
    private UUID deliveryId;
    private LocalDateTime createdAt;
    private String role;
    private String userEmail;
    private String action;
}
