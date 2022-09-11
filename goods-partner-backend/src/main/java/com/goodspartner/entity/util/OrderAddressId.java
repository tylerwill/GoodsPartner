package com.goodspartner.entity.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderAddressId implements Serializable {
    private String orderAddress;
    private String clientName;
}