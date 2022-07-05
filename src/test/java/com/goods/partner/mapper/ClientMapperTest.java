package com.goods.partner.mapper;

import com.goods.partner.dto.AddressDto;
import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.ClientDto;
import com.goods.partner.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ClientMapperTest {

    private final ClientMapper clientMapper = new ClientMapper();
    private static Order mockOrder;
    private static Address address;
    private static List<Address> addressList;
    private static Client client;


    @BeforeAll
    static void setup() {
        Product mockProduct = mock(Product.class);
        when(mockProduct.getKg()).thenReturn(10D);


        OrderedProduct mockOrderedProduct = mock(OrderedProduct.class);
        when(mockOrderedProduct.getCount()).thenReturn(5);
        when(mockOrderedProduct.getProduct()).thenReturn(mockProduct);


        mockOrder = mock(Order.class);
        when(mockOrder.getId()).thenReturn(1);
        when(mockOrder.getNumber()).thenReturn(1);
        when(mockOrder.getOrderedProducts()).thenReturn(List.of(mockOrderedProduct));


        address = mock(Address.class);
        when(address.getOrders()).thenReturn(List.of(mockOrder));
        when(address.getAddress()).thenReturn("м. Київ, вул. Хрещатик, 1");

        addressList = List.of(address);

        client = mock(Client.class);
        when(client.getId()).thenReturn(1);
        when(client.getName()).thenReturn("ТОВ \"Хлібзавод\"");
        when(client.getAddresses()).thenReturn(addressList);
    }


    @Test
    @DisplayName("Mapping Order to AddressDto")
    void test_givenOrder_whenMapAddressOrder_thenReturnAddressOrderDto() {

        AddressOrderDto addressOrderDto = clientMapper.mapAddressOrder(mockOrder);

        assertEquals(mockOrder.getId(), addressOrderDto.getOrderId());
        assertEquals(mockOrder.getNumber(), addressOrderDto.getOrderNumber());
        assertEquals(50, addressOrderDto.getOrderTotalWeight(), 0.001);
    }


    @Test
    @DisplayName("Mapping Order list to AddressDto list")
    void test_givenListOrder_whenMapAddressOrder_thenReturnListAddressOrderDto() {

        List<Order> orderList = List.of(mockOrder);

        List<AddressOrderDto> addressOrderDtoList = clientMapper.mapOrdersToAddress(orderList);


        assertEquals(1, addressOrderDtoList.size());

        AddressOrderDto addressOrderDto = addressOrderDtoList.get(0);

        assertEquals(mockOrder.getId(), addressOrderDto.getOrderId());
        assertEquals(mockOrder.getNumber(), addressOrderDto.getOrderNumber());
        assertEquals(50, addressOrderDto.getOrderTotalWeight());

    }


    @Test
    @DisplayName("Mapping Address to AddressDto")
    void test_givenAddress_whenMapAddress_thenReturnAddressDto() {

        AddressDto addressDto = clientMapper.mapAddress(address);

        assertEquals("м. Київ, вул. Хрещатик, 1", addressDto.getAddress());
        assertEquals(1, addressDto.getOrders().size());
        assertEquals(50, addressDto.getAddressTotalWeight(), 0.001);

    }


    @Test
    @DisplayName("Mapping List Address list to AddressDto")
    void test_givenAddressList_whenMapAddresses_thenReturnListAddressDto() {

        List<AddressDto> addressDtoList = clientMapper.mapAddresses(addressList);

        assertEquals(1, addressDtoList.size());

        AddressDto addressDto = addressDtoList.get(0);

        assertEquals(address.getAddress(), addressDto.getAddress());
        assertEquals(address.getOrders().size(), addressDto.getOrders().size());
        assertEquals(50, addressDto.getAddressTotalWeight(), 0.001);

    }


    @Test
    @DisplayName("Mapping Client to ClientDto")
    void test_givenClient_whenMapClient_thenReturnClientDto() {

        ClientDto clientDto = clientMapper.mapClient(client);

        assertEquals(client.getName(), clientDto.getClientName());
        assertEquals(client.getId(), clientDto.getClientId());
        assertEquals(client.getAddresses().size(), clientDto.getAddresses().size());


        List<AddressDto> addresses = clientDto.getAddresses();
        assertEquals(address.getAddress(), addresses.get(0).getAddress());
        assertEquals(50, addresses.get(0).getAddressTotalWeight(), 0.001);

    }


    @Test
    @DisplayName("Mapping Client list to ClientDto list")
    void test_givenClientList_whenMapClients_thenReturnClientDtoList() {

        List<Client> clientList = List.of(client);
        List<ClientDto> clientDtoList = clientMapper.mapClients(clientList);


        assertEquals(clientList.size(), clientDtoList.size());


        ClientDto clientDto = clientDtoList.get(0);

        assertEquals(client.getName(), clientDto.getClientName());
        assertEquals(client.getId(), clientDto.getClientId());
        assertEquals(client.getAddresses().size(), clientDto.getAddresses().size());


        List<AddressDto> addresses = clientDto.getAddresses();
        assertEquals(address.getAddress(), addresses.get(0).getAddress());
        assertEquals(50, addresses.get(0).getAddressTotalWeight(), 0.001);

    }

    @Test
    @DisplayName("Mapping Client list to ClientDto list checking method calls")
    void test_givenClientList_whenMapClients_thenReturnClientDtoList_verify() {
        List<Client> clientList = List.of(client);

        ClientMapper spyClientMapper = spy(clientMapper);

        List<ClientDto> clientDtoList = spyClientMapper.mapClients(clientList);
        assertEquals(clientDtoList.size(), 1);


        verify(spyClientMapper, times(1)).mapAddressOrder(mockOrder);
        verify(spyClientMapper, times(1)).mapOrdersToAddress(List.of(mockOrder));
        verify(spyClientMapper, times(1)).mapAddress(address);
        verify(spyClientMapper, times(1)).mapAddresses(addressList);
        verify(spyClientMapper, times(1)).mapClient(client);

    }


}