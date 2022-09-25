package com.goodspartner.util;

import com.goodspartner.dto.Product;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.goodspartner.service.impl.MockedStoreService;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ExternalOrderDataEnricher {

    private final MockedStoreService storeService;

    public void enrichODataProduct(ODataProductDto product) {
        product.setUnitWeight(calculateProductUnitWeight(product));
        product.setTotalProductWeight(getTotalProductWeight(product));
        product.setStoreName(storeService.getMainStore().getName());
    }

    public void enrichProduct(Product product) {
        product.setTotalProductWeight(getTotalProductWeight(product));
        product.setStoreName(storeService.getMainStore().getName());
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
    double getTotalProductWeight(Product product) {
        return product.getAmount() * product.getUnitWeight();
    }
}
