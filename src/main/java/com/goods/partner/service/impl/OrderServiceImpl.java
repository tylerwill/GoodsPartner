package com.goods.partner.service.impl;

import com.goods.partner.dto.CalculationDto;
import com.goods.partner.dto.OrderData;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.ProductDto;
import com.goods.partner.entity.Address;
import com.goods.partner.entity.Client;
import com.goods.partner.entity.Manager;
import com.goods.partner.entity.Order;
import com.goods.partner.entity.OrderedProduct;
import com.goods.partner.entity.Product;
import com.goods.partner.entity.Store;
import com.goods.partner.repository.OrderRepository;
import com.goods.partner.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public CalculationDto calculate(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByDate(date);

        List<OrderDto> orderDto = mapOrders(ordersByDate);

        CalculationDto calculationDto = new CalculationDto();
        calculationDto.setOrders(orderDto);

        return calculationDto;
    }

    private List<OrderDto> mapOrders(List<Order> orders) {
        return orders.stream()
                .map(this::mapOrder)
                .collect(Collectors.toList());
    }

    private OrderDto mapOrder(Order order) {
        Address address = order.getAddress();
        Client client = address.getClient();

        Manager manager = order.getManager();

        List<ProductDto> products = mapProducts(order.getProducts());

        OrderData orderData = new OrderData();
        orderData.setClientName(client.getName());
        orderData.setAddress(address.getAddress());
        orderData.setManagerFullName(manager.getFirstName() + " " + manager.getLastName()); // TODO check with Taras to have single field for this
        orderData.setProducts(products);

        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId(order.getId());
        orderDto.setOrderNumber(order.getNumber());
        orderDto.setOrderData(orderData);
        return orderDto;
    }

    private List<ProductDto> mapProducts(List<OrderedProduct> products) {
        return products.stream()
                .map(this::mapProduct)
                .collect(Collectors.toList());
    }

    private ProductDto mapProduct(OrderedProduct orderedProduct) {
        Product product = orderedProduct.getProduct();
        Store store = product.getStore();

        ProductDto productDto = new ProductDto();
        productDto.setProductName(product.getName());
        productDto.setAmount(orderedProduct.getCount());
        productDto.setStoreName(store.getName());
        return productDto;
    }

}
