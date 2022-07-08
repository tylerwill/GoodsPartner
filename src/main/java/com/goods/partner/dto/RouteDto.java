package com.goods.partner.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteDto {

    private int routeId;
    private String status;
    private double totalWeight;
    private int totalPoints;
    private int totalOrders;
    private double distance;
    private LocalDateTime estimatedTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime spentTime;
    private String routeLink;
    private String storeName;
    private String storeAddress;

    private List<ClientDto> clients;
}