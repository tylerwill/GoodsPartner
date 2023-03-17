package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.goodspartner.dto.ProductMeasureDetails;
import com.goodspartner.mapper.util.MapperUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ODataInvoiceProductDto {
    @JsonAlias("Ref_Key")
    private String refKey;
    @JsonAlias("LineNumber")
    private String lineNumber;

    @JsonAlias("Количество") // TODO check what is @JsonAlias("КоличествоМест")
    private int amount;

    @JsonAlias("Коэффициент")
    private Double coefficient; // TODO Why do we need coefficient if we have coefficientStandard in Unit or Packaging
    @JsonAlias("Сумма")
    private Double priceAmount;
    @JsonAlias("СуммаНДС")
    private Double priceAmountPDV;
    @JsonAlias("Цена")
    private Double price;

    private String productName;
    private String productRefKey;
    private String productGTDRefKey;

    private String uktzedCode;

    private String qualityUrl;

    private String measure;
    private Double totalProductWeight;
    private ProductMeasureDetails productUnit;
    private ProductMeasureDetails productPackaging;

    @JsonProperty("Номенклатура")
    private void mapProductName(Map<String, Object> value) {
        this.productName = (String) value.get("Description");
        this.productRefKey = (String) value.get("Ref_Key");
        this.productGTDRefKey = (String) value.get("НоменклатураГТД");

        Map<String, Object> productUnit = (Map<String, Object>) value.get("ЕдиницаХраненияОстатков");
        Map<String, Object> productPackaging = (Map<String, Object>) value.get("ЕдиницаИзмеренияМест");

        this.productUnit = mapProductUnitDetails(productUnit);
        this.productPackaging = productPackaging != null
                ? mapProductPackagingDetails(productPackaging)
                : this.productUnit;
    }

    @JsonProperty("ЕдиницаИзмерения")
    private void mapMeasurer(Map<String, String> value) {
        String unit = value.get("Description");
        this.measure = unit != null ? unit : "";
    }

    @JsonProperty("СерияНоменклатуры")
    private void mapQualityUrl(Map<String, String> value) {
        this.qualityUrl = Optional.ofNullable(value)
                .map(series -> series.get("СертификатФайл"))
                .orElse("");
    }


    private ProductMeasureDetails mapProductUnitDetails(Map<String, Object> measureMap) {
        return ProductMeasureDetails.builder()
                .measureStandard((String) measureMap.get("Description"))
                .coefficientStandard(Double.valueOf(String.valueOf(measureMap.get("Коэффициент"))))
                .amount(amount * coefficient) // TODO check mapping order. TODO do we need here coefficientStandard?
                .build();
    }

    private ProductMeasureDetails mapProductPackagingDetails(Map<String, Object> measureMap) {
        ProductMeasureDetails productPackaging = ProductMeasureDetails.builder()
                .measureStandard((String) measureMap.get("Description"))
                .coefficientStandard(Double.valueOf(String.valueOf(measureMap.get("Коэффициент"))))
                .build();

        Double packagingCoefficient = productPackaging.getCoefficientStandard();
        productPackaging.setAmount(MapperUtil.getRoundedDouble(amount * coefficient / packagingCoefficient));

        return productPackaging;
    }

}
