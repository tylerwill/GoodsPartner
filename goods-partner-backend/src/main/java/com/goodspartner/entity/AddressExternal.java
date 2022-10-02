package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "grandedolce_addresses")
public class AddressExternal {

    @EmbeddedId
    private OrderAddressId orderAddressId;

    @Column(name = "valid_address")
    private String validAddress;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class OrderAddressId implements Serializable {

        @Column(name = "order_address")
        private String orderAddress;

        @Column(name = "client_name")
        private String clientName;
    }
}