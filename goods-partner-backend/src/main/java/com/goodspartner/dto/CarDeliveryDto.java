package com.goodspartner.dto;

import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class CarDeliveryDto {

    private UUID id;
    private LocalDate deliveryDate;
    private DeliveryStatus status;
    private DeliveryFormationStatus formationStatus;

    private List<RouteDto> routes;
    private List<OrderDto> orders;
    private List<CarLoadDto> carLoads; // TODO it doesn't sorted in route order

}
