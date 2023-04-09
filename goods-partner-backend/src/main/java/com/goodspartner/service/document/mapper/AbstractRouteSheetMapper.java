package com.goodspartner.service.document.mapper;

import com.goodspartner.dto.InvoiceDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public class AbstractRouteSheetMapper {

    @Named("mapInvoiceAmount")
    public String mapInvoiceAmount(InvoiceDto invoiceDto) {
        return invoiceDto.getBuhBaseProperty()
                ? StringUtils.EMPTY
                : String.valueOf(invoiceDto.getInvoiceAmount());
    }
}
