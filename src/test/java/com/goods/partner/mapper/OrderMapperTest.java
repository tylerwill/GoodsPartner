package com.goods.partner.mapper;

import com.goods.partner.dto.OrderData;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.ProductDto;
import com.goods.partner.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
class OrderMapperTest {

    private OrderMapper orderMapper;
    private OrderedProduct mockOrderedProduct;
    private Order mockOrder;

    @BeforeAll
    void setup() {
        orderMapper = new OrderMapper();

        Store mockStore = mock(Store.class);
        when(mockStore.getName()).thenReturn("Склад №1");

        Product mockProduct = mock(Product.class);
        when(mockProduct.getName()).thenReturn("3434 Паста шоколадна");
        when(mockProduct.getStore()).thenReturn(mockStore);

        mockOrderedProduct = mock(OrderedProduct.class);
        when(mockOrderedProduct.getProduct()).thenReturn(mockProduct);
        when(mockOrderedProduct.getCount()).thenReturn(5);

        Manager mockManager = mock(Manager.class);
        when(mockManager.getFirstName()).thenReturn("Петро");
        when(mockManager.getLastName()).thenReturn("Коваленко");

        Client mockClient = mock(Client.class);
        when(mockClient.getId()).thenReturn(1);
        when(mockClient.getName()).thenReturn("ТОВ \"Хлібзавод\"");

        Address mockAddress = mock(Address.class);
        when(mockAddress.getAddress()).thenReturn("м. Київ, вул. Хрещатик, 1");
        when(mockAddress.getClient()).thenReturn(mockClient);

        mockOrder = mock(Order.class);
        when(mockOrder.getId()).thenReturn(1);
        when(mockOrder.getNumber()).thenReturn(1);
        when(mockOrder.getManager()).thenReturn(mockManager);
        when(mockOrder.getOrderedProducts()).thenReturn(List.of(mockOrderedProduct));
        when(mockOrder.getCreatedDate()).thenReturn(LocalDate.of(2022, 6, 28));
        when(mockOrder.getAddress()).thenReturn(mockAddress);
    }

    @Test
    @DisplayName("Mapping OrderedProduct to ProductDto")
    void test_givenOrderedProduct_whenMapProduct_thenReturnProductDto() {
        ProductDto productDto = orderMapper.mapProduct(mockOrderedProduct);
        assertEquals("3434 Паста шоколадна", productDto.getProductName());
        assertEquals(5, productDto.getAmount());
        assertEquals("Склад №1", productDto.getStoreName());
    }

    @Test
    @DisplayName("Mapping OrderedProduct list to ProductDto list")
    void test_givenOrderedProductList_whenMapProducts_thenReturnProductDtoList() {
        OrderMapper spyOrderMapper = spy(orderMapper);
        List<ProductDto> productDtoList = spyOrderMapper
                .mapProducts(List.of(mockOrderedProduct, mockOrderedProduct, mockOrderedProduct));

        assertEquals(3, productDtoList.size());
        verify(spyOrderMapper).mapProducts(anyList());
        verify(spyOrderMapper, times(3)).mapProduct(any(OrderedProduct.class));

    }

    @Test
    @DisplayName("Mapping Order to OrderDto")
    void test_givenOrder_whenMapOrder_thenReturnOrderDto() {
        OrderMapper spyOrderMapper = spy(orderMapper);
        OrderDto orderDto = spyOrderMapper.mapOrder(mockOrder);
        OrderData orderData = orderDto.getOrderData();
        List<ProductDto> products = orderData.getProducts();

        assertEquals(1, orderDto.getOrderId());
        assertEquals("1", orderDto.getOrderNumber());
        assertEquals(LocalDate.of(2022, 6, 28), orderDto.getCreatedDate());
        assertEquals("ТОВ \"Хлібзавод\"", orderData.getClientName());
        assertEquals("м. Київ, вул. Хрещатик, 1", orderData.getAddress());
        assertEquals("Петро Коваленко", orderData.getManagerFullName());
        assertEquals(1, products.size());
        verify(spyOrderMapper).mapProducts(anyList());
        verify(spyOrderMapper).mapProduct(mockOrderedProduct);

    }

    @Test
    @DisplayName("Mapping Order list to OrderDto list checking method calls")
    void test_givenOrderList_whenMapOrder_thenReturnOrderDtoListVerify() {
        OrderMapper spyOrderMapper = spy(orderMapper);
        List<OrderDto> orderDtoList = spyOrderMapper
                .mapOrders(List.of(mockOrder, mockOrder, mockOrder));

        assertEquals(3, orderDtoList.size());
        verify(spyOrderMapper).mapOrders(anyList());
        verify(spyOrderMapper, times(3)).mapProduct(mockOrderedProduct);
        verify(spyOrderMapper, times(3)).mapProducts(anyList());
        verify(spyOrderMapper, times(3)).mapOrder(mockOrder);

    }

}
