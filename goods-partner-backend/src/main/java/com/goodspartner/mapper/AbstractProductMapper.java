package com.goodspartner.mapper;

import com.goodspartner.dto.ProductMeasureDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public abstract class AbstractProductMapper {
    @Named("mapProductUnit")
    public ProductMeasureDetails mapProductUnit(ProductMeasureDetails productUnit) {
        return ProductMeasureDetails.builder()
                .measureStandard(productUnit.getMeasureStandard())
                .coefficientStandard(productUnit.getCoefficientStandard())
                .amount(productUnit.getAmount())
                .build();
    }

    @Named("mapProductPackaging")
    public ProductMeasureDetails mapProductPackaging(ProductMeasureDetails productPackaging) {
        return ProductMeasureDetails.builder()
                .measureStandard(productPackaging.getMeasureStandard())
                .coefficientStandard(productPackaging.getCoefficientStandard())
                .amount(productPackaging.getAmount())
                .build();
    }
}
