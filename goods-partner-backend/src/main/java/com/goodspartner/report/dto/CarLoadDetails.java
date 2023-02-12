package com.goodspartner.report.dto;

import com.goodspartner.entity.Car;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class CarLoadDetails {
    private Car car;
    private Map<String, List<ProductLoadDetails>> carLoadProducts;
}
