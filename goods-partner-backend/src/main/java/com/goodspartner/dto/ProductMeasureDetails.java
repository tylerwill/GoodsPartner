package com.goodspartner.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ProductMeasureDetails {
    private String measureStandard;
    private Double coefficientStandard;
    private Double amount;
}
