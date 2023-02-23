package com.goodspartner.mapper.decorator;

import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.dto.ProductMeasureDetails;
import com.goodspartner.mapper.InvoiceProductMapper;
import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceProductDto;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class InvoiceProductDecorator implements InvoiceProductMapper {

    @Qualifier("invoiceProductMapperImpl_")
    @Autowired
    private InvoiceProductMapper delegate;

    @Override
    public InvoiceProduct map(ODataInvoiceProductDto oDataInvoiceProductDto) {
        InvoiceProduct invoiceProduct = delegate.map(oDataInvoiceProductDto);
        setProductPackagingAmount(invoiceProduct);
        return invoiceProduct;
    }

    @Override
    public List<InvoiceProduct> toInvoiceProductList(List<ODataInvoiceProductDto> oDataInvoiceProductDtoList) {
        return oDataInvoiceProductDtoList.stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private void setProductPackagingAmount(InvoiceProduct product) {
        ProductMeasureDetails productPackaging = product.getProductPackaging();
        Double packagingCoefficient = productPackaging.getCoefficientStandard();
        Double unitAmount = product.getProductUnit().getAmount();
        product.getProductPackaging().setAmount(unitAmount / packagingCoefficient);
    }
}
