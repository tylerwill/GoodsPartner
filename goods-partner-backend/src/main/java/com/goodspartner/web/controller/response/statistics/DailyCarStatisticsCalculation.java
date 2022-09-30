package com.goodspartner.web.controller.response.statistics;

import com.goodspartner.dto.CarDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DailyCarStatisticsCalculation {

    private int orderCount;
    private CarDto car;
}
