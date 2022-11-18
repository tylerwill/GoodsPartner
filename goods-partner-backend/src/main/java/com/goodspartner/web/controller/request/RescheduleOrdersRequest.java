package com.goodspartner.web.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class RescheduleOrdersRequest {

    private LocalDate rescheduleDate;

    private List<Integer> orderIds;

}
