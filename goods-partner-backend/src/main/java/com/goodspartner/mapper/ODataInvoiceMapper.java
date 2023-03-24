package com.goodspartner.mapper;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.mapper.decorator.ODataInvoiceMapperDecorator;
import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {AbstractODataInvoiceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@DecoratedWith(ODataInvoiceMapperDecorator.class)
public interface ODataInvoiceMapper {

    @Mapping(target = "number", expression = "java(String.valueOf(Integer.parseInt(oDataInvoiceDto.getNumber())))")
    @Mapping(target = "documentDate", source = "oDataInvoiceDto.documentDate", qualifiedByName = "mapDocumentDate")
    @Mapping(target = "companyInformation", source = "oDataInvoiceDto", qualifiedByName = "mapCompanyInformation")
    @Mapping(target = "orderInfo", source = "oDataInvoiceDto", qualifiedByName = "mapOrderInfo")
    @Mapping(target = "invoiceAmountPDV", source = "oDataInvoiceDto", qualifiedByName = "mapInvoiceAmountPDV")
    @Mapping(target = "orderNumber", expression = "java(String.valueOf(Integer.parseInt(oDataInvoiceDto.getOrder().getOrderNumber())))")
    @Mapping(target = "orderDate", source = "oDataInvoiceDto.order", qualifiedByName = "mapOrderDate")
    @Mapping(target = "bankName", source ="oDataInvoiceDto.bankName")
    @Mapping(target = "shippingDate", source ="oDataInvoiceDto.order.shippingDate")
    @Mapping(target = "companyAccount", source ="oDataInvoiceDto.companyAccount")
    @Mapping(target = "edrpouCode", expression = "java(oDataInvoiceDto.getOrganisationCodes().getEdrpouCode())")
    @Mapping(target = "mfoCode", expression ="java(oDataInvoiceDto.getMfoCode().trim())")
    InvoiceDto map(ODataInvoiceDto oDataInvoiceDto);

    List<InvoiceDto> mapList(List<ODataInvoiceDto> oDataInvoiceDtos);
}
