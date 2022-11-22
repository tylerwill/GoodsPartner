package com.goodspartner.web.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.goodspartner.entity.DeliveryStatus;
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
public class RouteActionResponse {

    private UUID deliveryId;
    private DeliveryStatus deliveryStatus;

    private long routeId;
    private RouteStatus routeStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime routeFinishTime;

}