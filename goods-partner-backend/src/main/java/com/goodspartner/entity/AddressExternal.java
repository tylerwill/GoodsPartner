package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Getter
@Setter
@Entity
@Table(name = "grandedolce_addresses")
public class AddressExternal { // TODO think about naming

    @EmbeddedId
    private OrderAddressId orderAddressId;

    private String validAddress;
    private double latitude;
    private double longitude;


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    @Getter
    @Setter
    public static class OrderAddressId implements Serializable {
        private String orderAddress;
        private String clientName;
    }
}