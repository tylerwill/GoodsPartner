package com.goodspartner.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DistanceMatrix {

    private Long[][] distance;
    private Long[][] duration; // in minutes

}
