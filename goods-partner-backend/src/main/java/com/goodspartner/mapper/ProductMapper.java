package com.goodspartner.mapper;

import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    List<ProductDto> toProductDtosList(List<ODataProductDto> oDataProductDtoList);

}
