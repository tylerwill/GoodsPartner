package com.goods.partner.dto;

public class AddressOrderDto {
    private int orderId;
    private int orderNumber;
    private double orderTotalWeight;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderTotalWeight(double orderTotalWeight) {
        this.orderTotalWeight = orderTotalWeight;
    }

    public double getOrderTotalWeight() {
        return orderTotalWeight;
    }
}
