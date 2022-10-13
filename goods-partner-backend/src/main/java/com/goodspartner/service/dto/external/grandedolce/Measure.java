package com.goodspartner.service.dto.external.grandedolce;

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
        public double calculateTotalProductWeight(ODataProductDto product) {
            return product.getTotalProductWeight() * product.getCoefficient();
        }
    },
    PACK_BOX(List.of("паков", "ящ")) {
        @Override
        public double calculateTotalProductWeight(ODataProductDto product) {
            double totalProductWeight = product.getTotalProductWeight();
            if (isPCS(product)) {
                return totalProductWeight;
            }

            return totalProductWeight * product.getCoefficient();
        }
    },
    PCS(List.of("шт")) {
        @Override
        public double calculateTotalProductWeight(ODataProductDto product) {
            int amount = product.getAmount();

            return amount == 0 || amount > 10 ? 10 : amount * 1.0;
        }
    },
    DEFAULT_MEASURE(Collections.emptyList()) {
        @Override
        public double calculateTotalProductWeight(ODataProductDto product) {
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

    public abstract double calculateTotalProductWeight(ODataProductDto product);

    private static boolean isPCS(ODataProductDto productDto) {
        String[] spellingOfPCS = new String[]{"шт", "штук"};
        double coefficient = productDto.getCoefficient();

        return StringUtils.indexOfAny(productDto.getProductName(), spellingOfPCS) != -1
                && (coefficient > 5 && (coefficient % 1 == 0));
    }
}
