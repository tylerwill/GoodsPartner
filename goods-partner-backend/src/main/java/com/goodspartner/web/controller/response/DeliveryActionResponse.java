package com.goodspartner.web.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.RouteStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryActionResponse {
    private UUID deliveryId;
    private DeliveryStatus deliveryStatus;

    private List<RoutesStatus> routesStatus;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoutesStatus {
        private long id;
        private RouteStatus routeStatus;
    }
}
