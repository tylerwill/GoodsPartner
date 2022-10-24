package com.goodspartner.dto;

import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private UUID id;
    private LocalDate deliveryDate;
    private DeliveryStatus status;
    private DeliveryFormationStatus formationStatus;

    private List<RouteDto> routes;
    private List<OrderDto> orders;
    private List<ProductShippingDto> productsShipping;
}
