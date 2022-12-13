package com.goodspartner.mapper;

import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {AbstractInvoiceProductMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceProductMapper {
    @Mapping(target = "totalProductWeight", expression = "java(String.valueOf(oDataInvoiceProductDto.getTotalProductWeight()))")
    @Mapping(target = "coefficient", expression = "java(String.valueOf(oDataInvoiceProductDto.getCoefficient()))")
    @Mapping(target = "amountWithoutPDV", source = "oDataInvoiceProductDto", qualifiedByName = "mapAmountWithoutPDV")
    @Mapping(target = "priceWithoutPDV", source = "oDataInvoiceProductDto", qualifiedByName = "mapPriceWithoutPDV")
    @Mapping(target = "priceAmount", expression = "java(String.valueOf(oDataInvoiceProductDto.getPriceAmount()))")
    @Mapping(target = "price", expression = "java(String.valueOf(oDataInvoiceProductDto.getPrice()))")
    InvoiceProduct map(ODataInvoiceProductDto oDataInvoiceProductDto);

    List<InvoiceProduct> toInvoiceProductList(List<ODataInvoiceProductDto> oDataInvoiceProductDtoList);
}
