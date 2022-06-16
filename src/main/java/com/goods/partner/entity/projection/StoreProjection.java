package com.goods.partner.entity.projection;

public class StoreProjection {

    private int storeId;
    private String storeName;
    private int orderId;
    private int orderNumber;
    private double totalOrderWeight;

    public StoreProjection(int storeId, String storeName, int orderId, int orderNumber, double totalOrderWeight) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.totalOrderWeight = totalOrderWeight;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public double getTotalOrderWeight() {
        return totalOrderWeight;
    }
}
