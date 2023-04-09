package com.goodspartner.service.document.mapper;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.RoutePoint;
import com.goodspartner.service.document.dto.RouteSheet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {AbstractRouteSheetMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RouteSheetMapper {

    @Mapping(target = "clientName", source = "routePoint.clientName")
    @Mapping(target = "address", source = "routePoint.address")
    @Mapping(target = "expectedArrival", source = "routePoint.expectedArrival")
    @Mapping(target = "expectedCompletion", source = "routePoint.expectedCompletion")
    @Mapping(target = "comment", source = "orderExternal.comment")
    @Mapping(target = "invoiceAmount", source = "invoiceDto", qualifiedByName = "mapInvoiceAmount")
    @Mapping(target = "invoiceNumber", source = "invoiceDto.number")
    RouteSheet map(RoutePoint routePoint, OrderExternal orderExternal, InvoiceDto invoiceDto);

    @Mapping(target = "clientName", source = "orderExternal.clientName")
    @Mapping(target = "address", source = "orderExternal.address")
    @Mapping(target = "comment", source = "orderExternal.comment")
    @Mapping(target = "invoiceAmount", source = "invoiceDto", qualifiedByName = "mapInvoiceAmount")
    @Mapping(target = "invoiceNumber", source = "invoiceDto.number")
    RouteSheet map(OrderExternal orderExternal, InvoiceDto invoiceDto);
}
