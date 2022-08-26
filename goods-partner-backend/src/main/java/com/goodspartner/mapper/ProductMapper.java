package com.goodspartner.mapper;

import com.goodspartner.dto.ProductDto;
import com.goodspartner.entity.OrderedProduct;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toProductDto(ODataProductDto oDataProductDto);

    List<ProductDto> toProductDtosList(List<ODataProductDto> oDataProductDtoList);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "amount", source = "count")
    @Mapping(target = "unitWeight", source = "product.kg")
    ProductDto mapProduct(OrderedProduct orderedProduct);

    List<ProductDto> mapProducts(List<OrderedProduct> products);
}
