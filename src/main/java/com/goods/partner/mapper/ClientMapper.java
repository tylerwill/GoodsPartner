package com.goods.partner.mapper;

import com.goods.partner.dto.AddressDto;
import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.ClientDto;
import com.goods.partner.entity.Address;
import com.goods.partner.entity.Client;
import com.goods.partner.entity.Order;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {


    public List<ClientDto> mapClients(List<Client> clients) {
        return clients.stream()
                .map(client -> mapClient(client))
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    ClientDto mapClient(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setClientId(client.getId());
        clientDto.setClientName(client.getName());
        clientDto.setAddresses(mapAddresses(client.getAddresses()));
        return clientDto;
    }

    @VisibleForTesting
    List<AddressDto> mapAddresses(List<Address> addresses) {
        return addresses.stream()
                .map(address -> mapAddress(address))
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    AddressDto mapAddress(Address address) {

        List<Order> orders = address.getOrders();
        List<AddressOrderDto> addressOrders = mapOrdersToAddress(orders);

        double addressTotalWeight = addressOrders.stream()
                .map(AddressOrderDto::getOrderTotalWeight)
                .collect(Collectors.summarizingDouble(weight -> weight))
                .getSum();

        AddressDto addressDto = new AddressDto();
        addressDto.setAddress(address.getAddress());
        addressDto.setOrders(addressOrders);
        addressDto.setAddressTotalWeight(addressTotalWeight);

        return addressDto;

    }

    @VisibleForTesting
    List<AddressOrderDto> mapOrdersToAddress(List<Order> orders) {
        return orders.stream()
                .map(this::mapAddressOrder)
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    AddressOrderDto mapAddressOrder(Order order) {
        double sum = order.getOrderedProducts()
                .stream()
                .map(orderedProduct -> orderedProduct.getCount() * orderedProduct.getProduct().getKg()) // TODO NPE not safe
                .collect(Collectors.summarizingDouble(kg -> kg))
                .getSum();

        AddressOrderDto addressOrderDto = new AddressOrderDto();
        addressOrderDto.setOrderId(order.getId());
        addressOrderDto.setOrderNumber(order.getNumber());
        addressOrderDto.setOrderTotalWeight(sum);
        return addressOrderDto;
    }
}
