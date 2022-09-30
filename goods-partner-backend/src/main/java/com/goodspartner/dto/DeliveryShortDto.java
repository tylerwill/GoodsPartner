package com.goodspartner.dto;

import com.goodspartner.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryShortDto {
    private UUID id;
    private LocalDate deliveryDate;
    private DeliveryStatus status;
    private int orderCount;
    private int routeCount;
}
