package com.goodspartner.mapper;

import com.goodspartner.dto.OrderData;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.*;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public List<OrderDto> mapOrders(List<Order> orders) {
        return orders.stream()
                .map(this::mapOrder)
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    OrderDto mapOrder(Order order) {
        Address address = order.getAddress();
        Client client = address.getClient();

        Manager manager = order.getManager();

        List<ProductDto> products = mapProducts(order.getOrderedProducts());
        double orderWeight = products.stream()
                .mapToDouble(ProductDto::getTotalProductWeight)
                .sum();

        OrderData orderData = new OrderData();
        orderData.setClientName(client.getName());
        orderData.setAddress(address.getAddress());
        orderData.setManagerFullName(manager.getFirstName() + " " + manager.getLastName()); // TODO check with Taras to have single field for this
        orderData.setProducts(products);
        orderData.setOrderWeight(orderWeight);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getId());
        orderDto.setCreatedDate(order.getCreatedDate());
        orderDto.setOrderNumber(String.valueOf(order.getNumber()));
        orderDto.setOrderData(orderData);
        return orderDto;
    }

    @VisibleForTesting
    List<ProductDto> mapProducts(List<OrderedProduct> products) {
        return products.stream()
                .map(this::mapProduct)
                .collect(Collectors.toList());
    }

    @VisibleForTesting
    ProductDto mapProduct(OrderedProduct orderedProduct) {
        Product product = orderedProduct.getProduct();
        Store store = product.getStore();

        ProductDto productDto = new ProductDto();
        productDto.setProductName(product.getName());
        productDto.setAmount(orderedProduct.getCount());
        productDto.setStoreName(store.getName());
        productDto.setUnitWeight(product.getKg());
        productDto.setTotalProductWeight((double) orderedProduct.getCount() * product.getKg());
        return productDto;
    }

}
