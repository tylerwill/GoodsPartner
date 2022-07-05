package com.goods.partner.mapper;

import com.goods.partner.dto.OrderData;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.ProductDto;
import com.goods.partner.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapper();
    private static Store mockStore;
    private static Product mockProduct;
    private static OrderedProduct mockOrderedProduct;
    private static Manager mockManager;
    private static Client mockClient;
    private static Address mockAddress;
    private static Order mockOrder;

    @BeforeAll
    static void setup() {

        mockStore = mock(Store.class);
        when(mockStore.getName()).thenReturn("Склад №1");


        mockProduct = mock(Product.class);
        when(mockProduct.getStore()).thenReturn(mockStore);


        mockOrderedProduct = mock(OrderedProduct.class);
        when(mockOrderedProduct.getProduct()).thenReturn(mockProduct);
        when(mockOrderedProduct.getCount()).thenReturn(5);


        mockManager = mock(Manager.class);
        when(mockManager.getFirstName()).thenReturn("Петро");
        when(mockManager.getLastName()).thenReturn("Коваленко");


        mockClient = mock(Client.class);
        when(mockClient.getId()).thenReturn(1);
        when(mockClient.getName()).thenReturn("ТОВ \"Хлібзавод\"");


        mockAddress = mock(Address.class);
        when(mockAddress.getAddress()).thenReturn("м. Київ, вул. Хрещатик, 1");
        when(mockAddress.getClient()).thenReturn(mockClient);


        mockOrder = mock(Order.class);
        when(mockOrder.getId()).thenReturn(1);
        when(mockOrder.getNumber()).thenReturn(1);
        when(mockOrder.getManager()).thenReturn(mockManager);
        when(mockOrder.getOrderedProducts()).thenReturn(List.of(mockOrderedProduct));
        when(mockOrder.getCreatedDate()).thenReturn(LocalDate.of(2022, 01, 01));
        when(mockOrder.getAddress()).thenReturn(mockAddress);
    }


    @Test
    @DisplayName("Mapping OrderedProduct to ProductDto")
    void test_givenOrderedProduct_whenMapProduct_thenReturnProductDto() {

        ProductDto productDto = orderMapper.mapProduct(mockOrderedProduct);

        assertEquals(mockProduct.getName(), productDto.getProductName());
        assertEquals(mockOrderedProduct.getCount(), productDto.getAmount());
        assertEquals(mockStore.getName(), productDto.getStoreName());
    }


    @Test
    @DisplayName("Mapping OrderedProduct list to ProductDto list")
    void test_givenOrderedProductList_whenMapProducts_thenReturnProductDtoList() {

        List<ProductDto> productDtoList = orderMapper.mapProducts(List.of(mockOrderedProduct));

        assertEquals(productDtoList.size(), 1);

        ProductDto productDto = productDtoList.get(0);

        assertEquals(mockProduct.getName(), productDto.getProductName());
        assertEquals(mockOrderedProduct.getCount(), productDto.getAmount());
        assertEquals(mockStore.getName(), productDto.getStoreName());
    }


    @Test
    @DisplayName("Mapping Ordered to OrderDto")
    void test_givenOrder_whenMapOrder_thenReturnOrderDto() {

        OrderDto orderDto = orderMapper.mapOrder(mockOrder);

        assertEquals(mockOrder.getId(), orderDto.getOrderId());
        assertEquals(mockOrder.getNumber(), orderDto.getOrderNumber());
        assertEquals(mockOrder.getCreatedDate(), orderDto.getCreatedDate());

        OrderData orderData = orderDto.getOrderData();


        assertEquals(mockClient.getName(), orderData.getClientName());
        assertEquals(mockAddress.getAddress(), orderData.getAddress());
        assertEquals(mockManager.getFirstName() + " " + mockManager.getLastName(), orderData.getManagerFullName());

        List<ProductDto> products = orderData.getProducts();
        assertEquals(products.size(), 1);
        ProductDto productDto = products.get(0);

        assertEquals(productDto.getAmount(), mockOrderedProduct.getCount());
        assertEquals(productDto.getProductName(), mockProduct.getName());
        assertEquals(productDto.getStoreName(), mockStore.getName());

    }


    @Test
    @DisplayName("Mapping Ordered list to OrderDto list")
    void test_givenOrderList_whenMapOrder_thenReturnOrderDtoList() {

        List<OrderDto> orderDtoList = orderMapper.mapOrders(List.of(mockOrder));

        assertEquals(orderDtoList.size(), 1);

        OrderDto orderDto = orderDtoList.get(0);

        assertEquals(mockOrder.getId(), orderDto.getOrderId());
        assertEquals(mockOrder.getNumber(), orderDto.getOrderNumber());
        assertEquals(mockOrder.getCreatedDate(), orderDto.getCreatedDate());

    }

    @Test
    @DisplayName("Mapping Ordered list to OrderDto list checking method calls")
    void test_givenOrderList_whenMapOrder_thenReturnOrderDtoListVerify() {

        OrderMapper spyOrderMapper = spy(orderMapper);

        List<OrderDto> orderDtoList = spyOrderMapper.mapOrders(List.of(mockOrder));

        assertEquals(orderDtoList.size(), 1);

        verify(spyOrderMapper, times(1)).mapProduct(mockOrderedProduct);
        verify(spyOrderMapper, times(1)).mapProducts(List.of(mockOrderedProduct));
        verify(spyOrderMapper, times(1)).mapOrder(mockOrder);

    }

}