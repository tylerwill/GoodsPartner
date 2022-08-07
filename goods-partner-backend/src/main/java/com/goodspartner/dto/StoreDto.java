package com.goodspartner.dto;

import java.util.ArrayList;
import java.util.List;

public class StoreDto {

    private int storeId;
    private String storeName;
    private String storeAddress;
    private final List<StoreOrderDto> orders = new ArrayList<>();

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

    public List<StoreOrderDto> getOrders() {
        return orders;
    }

    public void addStoreOrderDto(StoreOrderDto storeOrderDto){
        orders.add(storeOrderDto);
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }
}
