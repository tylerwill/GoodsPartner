package com.goodspartner.mapper;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.Manager;
import com.goodspartner.entity.Order;
import com.goodspartner.entity.OrderedProduct;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Autowired
    private ProductMapper productMapper;

    @Mapping(target = "orderNumber", source = "number")
    @Mapping(target = "clientName", source = "order.address.client.name")
    @Mapping(target = "address", source = "order.address.address")
    @Mapping(target = "managerFullName", source = "order.manager", qualifiedByName = "getManagerFullName")
    @Mapping(target = "products", source = "order.orderedProducts", qualifiedByName = "mapProducts")
    public abstract OrderDto mapOrder(Order order);

    public abstract List<OrderDto> mapOrders(List<Order> orders);

    @Named("getManagerFullName")
    String getManagerFullName(Manager manager) {
        return manager.getFirstName() + " " + manager.getLastName();
    }

    public abstract OrderDto toOrderDto(ODataOrderDto oDataOrderDto);

    public abstract List<OrderDto> toOrderDtosList(List<ODataOrderDto> oDataOrderDtos);

    @Named("mapProducts")
    List<ProductDto> mapProducts(List<OrderedProduct> orderedProducts) {
        return productMapper.mapProducts(orderedProducts);
    }

}
