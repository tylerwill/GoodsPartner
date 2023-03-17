package com.goodspartner.mapper;

import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.dto.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productUnit", source = "source.productUnit")
    @Mapping(target = "productPackaging", source = "source.productPackaging")
    Product invoiceProductToProduct(InvoiceProduct source);

    List<Product> invoiceProductToProductList(List<InvoiceProduct> oDataProductDtoList);

}
