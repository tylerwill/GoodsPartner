package com.goodspartner.mapper.decorator;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.mapper.ODataInvoiceMapper;
import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Mapper(componentModel = "spring")
public class ODataInvoiceMapperDecorator implements ODataInvoiceMapper {
    private static final Integer ROUND_SCALE = 2;

    @Qualifier("delegate")
    @Autowired
    private ODataInvoiceMapper delegate;

    @Override
    public InvoiceDto map(ODataInvoiceDto oDataInvoiceDto) {
        InvoiceDto invoice = delegate.map(oDataInvoiceDto);
        Double invoiceAmountWithoutPDV = invoice.getInvoiceAmount() - invoice.getInvoiceAmountPDV();
        invoice.setInvoiceAmountWithoutPDV(invoiceAmountWithoutPDV);

        ValueConverters converters = ValueConverters.UKRAINIAN_INTEGER;

        MoneyDto priceMoneyDto = getMoneyDto(invoice.getInvoiceAmount());
        String priceUnits = converters.asWords(priceMoneyDto.getUnits());
        String priceNanos = converters.asWords(priceMoneyDto.getNanos());

        MoneyDto pdvMoneyDto = getMoneyDto(invoice.getInvoiceAmountPDV());
        String pdvUnits = converters.asWords(pdvMoneyDto.getUnits());
        String pdvNanos = converters.asWords(pdvMoneyDto.getNanos());

        invoice.setTextNumeric(
                priceUnits + " гривень " + priceNanos + " копійок \n" +
                        "У т.ч. ПДВ: " + pdvUnits + " гривень " + pdvNanos + " копійок");
        return invoice;
    }

    @Override
    public List<InvoiceDto> mapList(List<ODataInvoiceDto> oDataInvoiceDtos) {
        return oDataInvoiceDtos.stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private MoneyDto getMoneyDto(Double doubleVal) {
        RoundingMode roundingMode = doubleVal > 0 ? RoundingMode.FLOOR : RoundingMode.CEILING;
        BigDecimal bigDecimalVal = new BigDecimal(String.valueOf(doubleVal)).setScale(ROUND_SCALE, RoundingMode.HALF_UP);
        int intPart = bigDecimalVal.intValue();
        int nanoPart = bigDecimalVal
                .subtract(bigDecimalVal.setScale(0, roundingMode))
                .movePointRight(bigDecimalVal.scale())
                .intValue();
        return MoneyDto.builder().units(intPart).nanos(nanoPart).build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class MoneyDto {
        private int units;
        private int nanos;
    }
}
