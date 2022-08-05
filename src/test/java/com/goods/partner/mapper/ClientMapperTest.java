package com.goods.partner.mapper;

import com.goods.partner.dto.AddressDto;
import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.ClientDto;
import com.goods.partner.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
class ClientMapperTest {

    private ClientMapper clientMapper;
    private Order mockOrder;
    private Address mockAddress;
    private Client client;

    @BeforeAll
    void setup() {
        clientMapper = new ClientMapper();
        Product mockProduct = mock(Product.class);
        when(mockProduct.getKg()).thenReturn(10.5);

        OrderedProduct mockOrderedProduct = mock(OrderedProduct.class);
        when(mockOrderedProduct.getCount()).thenReturn(5);
        when(mockOrderedProduct.getProduct()).thenReturn(mockProduct);

        mockOrder = mock(Order.class);
        when(mockOrder.getId()).thenReturn(1);
        when(mockOrder.getNumber()).thenReturn(1);
        when(mockOrder.getOrderedProducts()).thenReturn(List.of(mockOrderedProduct));

        mockAddress = mock(Address.class);
        when(mockAddress.getOrders()).thenReturn(List.of(mockOrder));
        when(mockAddress.getAddress()).thenReturn("м. Київ, вул. Хрещатик, 1");

        client = mock(Client.class);
        when(client.getId()).thenReturn(1);
        when(client.getName()).thenReturn("ТОВ \"Хлібзавод\"");
        when(client.getAddresses()).thenReturn(List.of(mockAddress));
    }

    @Test
    @DisplayName("Mapping Order to AddressDto")
    void test_givenOrder_whenMapAddressOrder_thenReturnAddressOrderDto() {
        AddressOrderDto addressOrderDto = clientMapper.mapAddressOrder(mockOrder);

        assertEquals(1, addressOrderDto.getOrderId());
        assertEquals("1", addressOrderDto.getOrderNumber());
        assertEquals(52.5, addressOrderDto.getOrderTotalWeight(), 0.001);
    }

    @Test
    @DisplayName("Mapping Order list to AddressDto list")
    void test_givenListOrder_whenMapAddressOrder_thenReturnListAddressOrderDto() {
        ClientMapper spyClientMapper = spy(clientMapper);
        List<AddressOrderDto> addressOrderDtoList = spyClientMapper
                .mapOrdersToAddress(List.of(mockOrder, mockOrder, mockOrder));

        assertEquals(3, addressOrderDtoList.size());
        verify(spyClientMapper).mapOrdersToAddress(anyList());
        verify(spyClientMapper, times(3)).mapAddressOrder(any(Order.class));
    }

    @Test
    @DisplayName("Mapping Address to AddressDto")
    void test_givenAddress_whenMapAddress_thenReturnAddressDto() {
        AddressDto addressDto = clientMapper.mapAddress(mockAddress);

        assertEquals("м. Київ, вул. Хрещатик, 1", addressDto.getAddress());
        assertEquals(1, addressDto.getOrders().size());
        assertEquals(52.5, addressDto.getAddressTotalWeight(), 0.001);
    }

    @Test
    @DisplayName("Mapping List Address list to AddressDto")
    void test_givenAddressList_whenMapAddresses_thenReturnListAddressDto() {
        ClientMapper spyClientMapper = spy(clientMapper);
        List<AddressDto> addressDtoList = spyClientMapper
                .mapAddresses(List.of(mockAddress, mockAddress, mockAddress));

        assertEquals(3, addressDtoList.size());
        verify(spyClientMapper).mapAddresses(anyList());
        verify(spyClientMapper, times(3)).mapAddress(any(Address.class));
    }

    @Test
    @DisplayName("Mapping Client to ClientDto")
    void test_givenClient_whenMapClient_thenReturnClientDto() {
        ClientMapper spyClientMapper = spy(clientMapper);
        ClientDto clientDto = spyClientMapper.mapClient(client);
        List<AddressDto> addresses = clientDto.getAddresses();

        assertEquals("ТОВ \"Хлібзавод\"", clientDto.getClientName());
        assertEquals(1, clientDto.getClientId());
        assertEquals(1, clientDto.getAddresses().size());
        verify(spyClientMapper).mapClient(client);
        verify(spyClientMapper).mapAddress(mockAddress);
        assertEquals("м. Київ, вул. Хрещатик, 1", addresses.get(0).getAddress());
        assertEquals(52.5, addresses.get(0).getAddressTotalWeight(), 0.001);
    }

    @Test
    @DisplayName("Mapping Client list to ClientDto list")
    void test_givenClientList_whenMapClients_thenReturnClientDtoList() {
        ClientMapper spyClientMapper = spy(clientMapper);
        List<ClientDto> clientDtoList = spyClientMapper
                .mapClients(List.of(client, client, client));

        assertEquals(3, clientDtoList.size());
        verify(spyClientMapper).mapClients(anyList());
        verify(spyClientMapper, times(3)).mapClient(any(Client.class));
    }

    @Test
    @DisplayName("Mapping Client list to ClientDto list checking method calls")
    void test_givenClientList_whenMapClients_thenReturnClientDtoList_verify() {
        List<Client> clientList = List.of(client);
        ClientMapper spyClientMapper = spy(clientMapper);
        List<ClientDto> clientDtoList = spyClientMapper.mapClients(clientList);

        assertEquals(1, clientDtoList.size());
        verify(spyClientMapper).mapAddressOrder(mockOrder);
        verify(spyClientMapper).mapOrdersToAddress(List.of(mockOrder));
        verify(spyClientMapper).mapAddress(mockAddress);
        verify(spyClientMapper).mapAddresses(List.of(mockAddress));
        verify(spyClientMapper).mapClient(client);
    }

}
