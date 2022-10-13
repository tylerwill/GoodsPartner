package com.goodspartner.util;

import com.goodspartner.dto.Product;
import com.goodspartner.service.StoreService;
import com.goodspartner.service.dto.external.grandedolce.Measure;
import com.goodspartner.service.dto.external.grandedolce.ODataProductDto;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ExternalOrderDataEnricher {

    private final StoreService storeService;

    public void enrichODataProduct(ODataProductDto product) {
        product.setTotalProductWeight(getTotalProductWeight(product));
        product.setUnitWeight(calculateProductUnitWeight(product));
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
        return Measure.of(product.getMeasure()).calculateTotalProductWeight(product);
    }

    @VisibleForTesting
    double getTotalProductWeight(Product product) {
        return product.getAmount() * product.getUnitWeight();
    }

}
