package com.goodspartner.web.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.RoutePointStatus;
import com.goodspartner.entity.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoutePointActionResponse {

    private UUID deliveryId;
    private DeliveryStatus deliveryStatus;

    private int routeId;
    private RouteStatus routeStatus;
    private LocalDateTime routeFinishTime;

    private UUID routePointId;
    private RoutePointStatus routePointStatus;
    private LocalDateTime pointCompletedAt;

}