package com.goods.partner.mapper;

import com.goods.partner.dto.AddressDto;
import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.ClientDto;
import com.goods.partner.entity.Address;
import com.goods.partner.entity.Client;
import com.goods.partner.entity.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    public List<ClientDto> mapClients(Set<Client> clients) {
        return clients.stream()
                .map(this::mapClient)
                .collect(Collectors.toList());
    }

    private ClientDto mapClient(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setClientId(client.getId());
        clientDto.setClientName(client.getName());
        clientDto.setAddresses(mapAddresses(client.getAddresses()));
        return clientDto;
    }

    private List<AddressDto> mapAddresses(List<Address> addresses) {
        return addresses.stream()
                .map(this::mapAddress)
                .collect(Collectors.toList());
    }

    private AddressDto mapAddress(Address address) {

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

    private List<AddressOrderDto> mapOrdersToAddress(List<Order> orders) {
        return orders.stream()
                .map(this::mapAddressOrder)
                .collect(Collectors.toList());
    }

    private AddressOrderDto mapAddressOrder(Order order) {
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
