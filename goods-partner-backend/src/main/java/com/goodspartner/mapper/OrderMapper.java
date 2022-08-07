package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.Address;
import com.goodspartner.entity.Client;
import com.goodspartner.entity.Manager;
import com.goodspartner.entity.Order;
import com.goodspartner.entity.OrderedProduct;
import com.goodspartner.entity.Product;
import com.goodspartner.factory.Store;
import com.goodspartner.factory.StoreFactory;
import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class OrderMapper {

    private final StoreFactory storeFactory;

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

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getId());
        orderDto.setCreatedDate(order.getCreatedDate());
        orderDto.setOrderNumber(String.valueOf(order.getNumber()));
        orderDto.setClientName(client.getName());
        orderDto.setAddress(address.getAddress());
        orderDto.setManagerFullName(manager.getFirstName() + " " + manager.getLastName()); // TODO check with Taras to have single field for this
        orderDto.setProducts(products);
        orderDto.setOrderWeight(orderWeight);

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
        Store store = storeFactory.getStore();

        ProductDto productDto = new ProductDto();
        productDto.setProductName(product.getName());
        productDto.setAmount(orderedProduct.getCount());
        productDto.setStoreName(store.getName());
        productDto.setUnitWeight(product.getKg());
        productDto.setTotalProductWeight((double) orderedProduct.getCount() * product.getKg());
        return productDto;
    }

}
