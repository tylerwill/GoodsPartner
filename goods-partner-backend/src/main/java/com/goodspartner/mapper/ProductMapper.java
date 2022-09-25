package com.goodspartner.mapper;

import com.goodspartner.dto.Product;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    List<Product> toProductDtosList(List<ODataProductDto> oDataProductDtoList);

}
