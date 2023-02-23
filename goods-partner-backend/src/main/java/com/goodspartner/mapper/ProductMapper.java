package com.goodspartner.mapper;

import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.dto.Product;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AbstractProductMapper.class})
public interface ProductMapper {

    List<Product> toProductList(List<ODataProductDto> oDataProductDtoList);

    @Mapping(target = "totalProductWeight", expression = "java(Double.parseDouble(source.getTotalProductWeight()))")
    @Mapping(target = "coefficient", expression = "java(Double.parseDouble(source.getCoefficient()))")
    @Mapping(target = "productUnit", source = "source.productUnit", qualifiedByName = "mapProductUnit")
    @Mapping(target = "productPackaging", source = "source.productPackaging", qualifiedByName = "mapProductPackaging")
    Product invoiceProductToProduct(InvoiceProduct source);

    List<Product> invoiceProductToProductList(List<InvoiceProduct> oDataProductDtoList);

}
