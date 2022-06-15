package com.goods.partner.service.impl;

import com.goods.partner.dto.AddressDto;
import com.goods.partner.dto.AddressOrderDto;
import com.goods.partner.dto.CalculationDto;
import com.goods.partner.dto.ClientDto;
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
import com.goods.partner.repository.ClientRepository;
import com.goods.partner.repository.OrderRepository;
import com.goods.partner.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public CalculationDto calculate(LocalDate date) {

        List<Order> ordersByDate = orderRepository.findAllByDateBefore(date);
        List<OrderDto> orderDtos = mapOrders(ordersByDate);

        Set<Client> clients = clientRepository.findClientsByOrderDate(date);
        log.info("clients Size: {}", clients.size());
        List<ClientDto> clientDtos = mapClients(clients);

        CalculationDto calculationDto = new CalculationDto();
        calculationDto.setDate(date);
        calculationDto.setOrders(orderDtos);
        calculationDto.setClients(clientDtos);

        return calculationDto;
    }


    /**
     *   MAPPING CLIENTS
     */

    private List<ClientDto> mapClients(Set<Client> clients) {
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
        double sum = order.getProducts()
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


    /**
     *   MAPPING ORDERS
     */

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
