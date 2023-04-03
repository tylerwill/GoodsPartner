package com.goodspartner.mapper;

import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.dto.ProductMeasureDetails;
import com.goodspartner.service.dto.external.grandedolce.Measure;
import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

import static com.goodspartner.mapper.util.MapperUtil.getRoundedDouble;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceProductMapper {

    int PDV_SEPARATE_VALUE = 6;

    @Mapping(target = "totalProductWeight", source = "oDataInvoiceProductDto", qualifiedByName = "mapTotalProductWeight")
    @Mapping(target = "amountWithoutPDV", source = "oDataInvoiceProductDto", qualifiedByName = "mapAmountWithoutPDV")
    @Mapping(target = "priceWithoutPDV", source = "oDataInvoiceProductDto", qualifiedByName = "mapPriceWithoutPDV")
    @Mapping(target = "priceAmount", expression = "java(String.valueOf(oDataInvoiceProductDto.getPriceAmount()))")
    @Mapping(target = "price", expression = "java(String.valueOf(oDataInvoiceProductDto.getPrice()))")
    InvoiceProduct map(ODataInvoiceProductDto oDataInvoiceProductDto);

    List<InvoiceProduct> toInvoiceProductList(List<ODataInvoiceProductDto> oDataInvoiceProductDtoList);

    @Named("mapAmountWithoutPDV")
    default String mapAmountWithoutPDV(ODataInvoiceProductDto oDataInvoiceProductDto) {
        double amount = oDataInvoiceProductDto.getPriceAmount() - oDataInvoiceProductDto.getPriceAmountPDV();
        return String.valueOf(getRoundedDouble(amount));
    }

    @Named("mapPriceWithoutPDV")
    default String mapPriceWithoutPDV(ODataInvoiceProductDto oDataInvoiceProductDto) {
        Double price = oDataInvoiceProductDto.getPrice();
        Double pricePdv = getPDV(price);
        Double priceWithoutPDV = price - pricePdv;
        return String.valueOf(getRoundedDouble(priceWithoutPDV));
    }

    @Named("mapTotalProductWeight")
    default Double mapTotalProductWeight(ODataInvoiceProductDto oDataInvoiceProductDto) {
        ProductMeasureDetails productUnit = oDataInvoiceProductDto.getProductUnit();
        String measureStandard = productUnit.getMeasureStandard();
        return getRoundedDouble(Measure.of(measureStandard).calculateTotalProductWeight(productUnit));
    }

    private Double getPDV(Double value) {
        return value / PDV_SEPARATE_VALUE;
    }
}
