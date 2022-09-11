package com.goodspartner.entity;

import com.goodspartner.entity.util.OrderAddressId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


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

}