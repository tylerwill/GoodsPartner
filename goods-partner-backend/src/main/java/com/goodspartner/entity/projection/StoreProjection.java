package com.goodspartner.entity.projection;

public class StoreProjection {

    private int storeId;
    private String storeName;
    private String storeAddress;
    private int orderId;
    private int orderNumber;
    private double totalOrderWeight;

    public StoreProjection(int storeId, String storeName, String storeAddress, int orderId, int orderNumber, double totalOrderWeight) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
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

    public String getStoreAddress() {
        return storeAddress;
    }
}
