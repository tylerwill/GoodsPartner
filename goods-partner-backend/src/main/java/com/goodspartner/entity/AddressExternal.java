package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "grandedolce_addresses")
public class AddressExternal {

    @EmbeddedId
    private OrderAddressId orderAddressId;

    @Enumerated(value = EnumType.STRING)
    private AddressStatus status;

    @Column(name = "valid_address")
    private String validAddress;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "service_time_minutes")
    private Integer serviceTimeMinutes; // default null to fetch default service time

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    @Getter
    @Setter
    public static class OrderAddressId implements Serializable {

        @Column(name = "order_address")
        private String orderAddress;

        @Column(name = "client_name")
        private String clientName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderAddressId that = (OrderAddressId) o;
            return orderAddress.equals(that.orderAddress) && clientName.equals(that.clientName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderAddress, clientName);
        }

        @Override
        public String toString() {
            return "OrderAddressId{" +
                    "orderAddress='" + orderAddress + '\'' +
                    ", clientName='" + clientName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AddressExternal{" +
                "orderAddressId=" + orderAddressId +
                ", status=" + status +
                ", validAddress='" + validAddress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}