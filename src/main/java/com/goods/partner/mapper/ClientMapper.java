package com.goods.partner.mapper;

import com.goods.partner.dto.AddressDto;
import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.ClientDto;
import com.goods.partner.entity.Address;
import com.goods.partner.entity.Client;
import com.goods.partner.entity.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClientMapper {


    public List<ClientDto> mapClients(Set<Client> clients, LocalDate date) {
        return clients.stream()
                .map(client -> mapClient(client,date))
                .collect(Collectors.toList());
    }

    private ClientDto mapClient(Client client, LocalDate date) {
        ClientDto clientDto = new ClientDto();
        clientDto.setClientId(client.getId());
        clientDto.setClientName(client.getName());
        clientDto.setAddresses(mapAddresses(client.getAddresses(), date));
        return clientDto;
    }

    private List<AddressDto> mapAddresses(List<Address> addresses, LocalDate date) {
        return addresses.stream()
                .map(address -> mapAddress(address, date))
                .collect(Collectors.toList());
    }

    private AddressDto mapAddress(Address address, LocalDate date) {

        List<Order> orders = address.getOrders();
        List<AddressOrderDto> addressOrders = mapOrdersToAddress(orders, date);

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

    private List<AddressOrderDto> mapOrdersToAddress(List<Order> orders, LocalDate localDate) {
        return orders.stream()
                .filter(order -> order.getShippingDate().equals(localDate))
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
