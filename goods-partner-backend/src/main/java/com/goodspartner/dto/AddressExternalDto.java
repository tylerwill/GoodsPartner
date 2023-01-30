package com.goodspartner.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AddressExternalDto {
    private String orderAddress;
    private String clientName;
    private String status;
    private String validAddress;
    private double latitude;
    private double longitude;
}
