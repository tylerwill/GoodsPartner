package com.goodspartner.service.dto.external.grandedolce;

import com.goodspartner.dto.ProductMeasureDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Slf4j
public enum Measure {

    KG_LITER_BUCKET_BANK(List.of("кг", "л", "відро", "банк")) {
        @Override
        public double calculateTotalProductWeight(ProductMeasureDetails product) {
            return product.getAmount() * product.getCoefficientStandard();
        }
    },

    PCS(List.of("шт")) {
        @Override
        public double calculateTotalProductWeight(ProductMeasureDetails product) {
            double amount = product.getAmount();
            return amount == 0 || amount > 10 ? 10 : amount;
        }
    },
    DEFAULT_MEASURE(Collections.emptyList()) {
        @Override
        public double calculateTotalProductWeight(ProductMeasureDetails product) {
            return 1.0;
        }
    };

    @Getter
    private final List<String> measure;

    public static Measure of(String measureName) {
        Measure[] measures = Measure.values();
        for (Measure measure : measures) {
            if (measure.getMeasure().contains(measureName)) {
                return measure;
            }
        }
        log.warn("Unknown measure: {}", measureName);

        return DEFAULT_MEASURE;
    }

    public abstract double calculateTotalProductWeight(ProductMeasureDetails product);

    // TODO I dont understand how this is working and why we need it now
    private static boolean isPCS(ODataProductDto productDto) {
        String[] spellingOfPCS = new String[]{"шт", "штук"};
        double coefficient = productDto.getCoefficient();

        return StringUtils.indexOfAny(productDto.getProductName(), spellingOfPCS) != -1
                && (coefficient > 5 && (coefficient % 1 == 0));
    }

    //    PACK_BOX(List.of("паков", "ящ")) {
//        @Override
//        public double calculateTotalProductWeight(ProductMeasureDetails product) {
//            double totalProductWeight = product.getTotalProductWeight();
//            if (isPCS(product)) {
//                return totalProductWeight;
//            }
//
//            return totalProductWeight * product.getCoefficient();
//        }
//    },
}
