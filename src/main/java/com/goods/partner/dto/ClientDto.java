package com.goods.partner.dto;

import java.util.List;

public class ClientDto {
    private int clientId;
    private String clientName;
    private List<AddressDto> addresses;

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setAddresses(List<AddressDto> addresses) {
        this.addresses = addresses;
    }

    public List<AddressDto> getAddresses() {
        return addresses;
    }
}
