package com.goodspartner.util;

import com.goodspartner.dto.OrderDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.impl.MockedStoreService;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DtoCalculationHelper {

    private final MockedStoreService storeService;

    public void enrichODataProduct(ODataProductDto product) {
        product.setUnitWeight(calculateProductUnitWeight(product));
        product.setTotalProductWeight(getTotalProductWeight(product));
        product.setStoreName(storeService.getMainStore().getName());
    }

    public void enrichProduct(ProductDto product) {
        product.setTotalProductWeight(getTotalProductWeight(product));
        product.setStoreName(storeService.getMainStore().getName());
    }

    public double calculateTotalOrderWeight(OrderDto order) {
        return order.getProducts().stream()
                .mapToDouble(ProductDto::getTotalProductWeight)
                .sum();
    }

    @VisibleForTesting
    double calculateProductUnitWeight(ODataProductDto product) {
        return product.getTotalProductWeight() / product.getAmount() * product.getCoefficient();
    }

    @VisibleForTesting
    double getTotalProductWeight(ODataProductDto product) {
        List<String> allowableMeasure = List.of("кг", "л", "шт");
        String measure = product.getMeasure();
        return allowableMeasure.contains(measure) ?
                (!measure.equals("шт") ? product.getTotalProductWeight() : 1.0) : 0.0;
    }

    @VisibleForTesting
    double getTotalProductWeight(ProductDto productDto) {
        return productDto.getAmount() * productDto.getUnitWeight();
    }
}
