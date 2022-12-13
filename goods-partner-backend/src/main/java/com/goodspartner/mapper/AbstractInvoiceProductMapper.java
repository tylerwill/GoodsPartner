package com.goodspartner.mapper;

import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import static com.goodspartner.mapper.util.MapperUtil.getRoundedDouble;

@Mapper
public abstract class AbstractInvoiceProductMapper {
    private static final int PDV_SEPARATE_VALUE = 6;

    @Named("mapAmountWithoutPDV")
    public String mapAmountWithoutPDV(ODataInvoiceProductDto oDataInvoiceProductDto) {
        double amount = oDataInvoiceProductDto.getPriceAmount() - oDataInvoiceProductDto.getPriceAmountPDV();
        return String.valueOf(getRoundedDouble(amount));
    }

    @Named("mapPriceWithoutPDV")
    public String mapPriceWithoutPDV(ODataInvoiceProductDto oDataInvoiceProductDto) {
        Double price = oDataInvoiceProductDto.getPrice();
        Double pricePdv = getPDV(price);
        Double priceWithoutPDV = price - pricePdv;
        return String.valueOf(getRoundedDouble(priceWithoutPDV));
    }

    private Double getPDV(Double value) {
        return value / PDV_SEPARATE_VALUE;
    }
}
