package com.goods.partner.dto;

import java.util.List;

public class AddressDto {
    private String address;
    private List<AddressOrderDto> orders;
    private double addressTotalWeight;

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setOrders(List<AddressOrderDto> orders) {
        this.orders = orders;
    }

    public List<AddressOrderDto> getOrders() {
        return orders;
    }

    public void setAddressTotalWeight(double addressTotalWeight) {
        this.addressTotalWeight = addressTotalWeight;
    }

    public double getAddressTotalWeight() {
        return addressTotalWeight;
    }
}
