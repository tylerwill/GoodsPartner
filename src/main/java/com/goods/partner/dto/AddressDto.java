package com.goods.partner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddressDto {
    private String address;
    private List<AddressOrderDto> orders;
    private double addressTotalWeight;
}
