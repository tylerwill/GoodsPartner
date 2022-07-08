package com.goods.partner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ClientDto {
    private int clientId;
    private String clientName;
    private List<AddressDto> addresses;
}