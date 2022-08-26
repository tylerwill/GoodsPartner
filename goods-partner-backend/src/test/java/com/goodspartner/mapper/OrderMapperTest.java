package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Address;
import com.goodspartner.entity.Client;
import com.goodspartner.entity.Manager;
import com.goodspartner.entity.Order;
import com.goodspartner.entity.OrderedProduct;
import com.goodspartner.entity.Product;
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
    private ProductMapper productMapper;
    private OrderedProduct mockOrderedProduct;
    private Order mockOrder;

    @BeforeAll
    void setup() {
        productMapper = new ProductMapperImpl();
        orderMapper = new OrderMapperImpl();
        orderMapper.setProductMapper(productMapper);

        StoreDto mockStoreDto = mock(StoreDto.class);
        when(mockStoreDto.getName()).thenReturn("Склад №1");

        Product mockProduct = mock(Product.class);
        when(mockProduct.getName()).thenReturn("3434 Паста шоколадна");
        when(mockProduct.getKg()).thenReturn(1.2);

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
    @DisplayName("Mapping OrderedProduct list to ProductDto list")
    void test_givenOrderedProductList_whenMapProducts_thenReturnProductDtoList() {
        OrderMapper spyOrderMapper = spy(orderMapper);
        ProductMapper spyProductMapper = spy(productMapper);
        List<ProductDto> productDtoList = spyOrderMapper
                .mapProducts(List.of(mockOrderedProduct, mockOrderedProduct, mockOrderedProduct));

        assertEquals(3, productDtoList.size());
        verify(spyOrderMapper).mapProducts(anyList());
    }

    @Test
    @DisplayName("Mapping Order to OrderDto")
    void test_givenOrder_whenMapOrder_thenReturnOrderDto() {
        OrderMapper spyOrderMapper = spy(orderMapper);
        OrderDto orderDto = spyOrderMapper.mapOrder(mockOrder);
        List<ProductDto> products = orderDto.getProducts();

        assertEquals(1, orderDto.getId());
        assertEquals("1", orderDto.getOrderNumber());
        assertEquals(LocalDate.of(2022, 6, 28), orderDto.getCreatedDate());
        assertEquals("ТОВ \"Хлібзавод\"", orderDto.getClientName());
        assertEquals("м. Київ, вул. Хрещатик, 1", orderDto.getAddress());
        assertEquals("Петро Коваленко", orderDto.getManagerFullName());
        assertEquals(1, products.size());
        verify(spyOrderMapper).mapProducts(anyList());

    }

    @Test
    @DisplayName("Mapping Order list to OrderDto list checking method calls")
    void test_givenOrderList_whenMapOrder_thenReturnOrderDtoListVerify() {
        OrderMapper spyOrderMapper = spy(orderMapper);
        List<OrderDto> orderDtoList = spyOrderMapper
                .mapOrders(List.of(mockOrder, mockOrder, mockOrder));

        assertEquals(3, orderDtoList.size());
        assertEquals(0.0, 0.0);
        verify(spyOrderMapper).mapOrders(anyList());
        verify(spyOrderMapper, times(3)).mapProducts(anyList());
        verify(spyOrderMapper, times(3)).mapOrder(mockOrder);
    }


    @Test
    @DisplayName("Mapping Order (with list of OrderedProduct) to OrderDto (with list of ProductDto) ")
    void test_givenOrderWithOrderedProductList_whenMapOrder_thenReturnOrderDtoWithProductDtoList() {
        Manager manager = new Manager();
        manager.setFirstName("First");
        manager.setLastName("Last");

        Client client = new Client();
        client.setName("Client name");

        Address address = new Address();
        address.setAddress("Test address");
        address.setClient(client);

        List<OrderedProduct> orderedProducts = List.of(new OrderedProduct(), new OrderedProduct());

        Order order = new Order();
        order.setManager(manager);
        order.setAddress(address);
        order.setOrderedProducts(orderedProducts);
        OrderDto orderDto = orderMapper.mapOrder(order);

        List<ProductDto> products = orderDto.getProducts();

        assertEquals("Client name", orderDto.getClientName());
        assertEquals("Test address", orderDto.getAddress());
        assertEquals("First Last", orderDto.getManagerFullName());
        assertEquals(orderedProducts.size(), products.size());
        assertEquals(ProductDto.class, products.get(0).getClass());
    }

}
