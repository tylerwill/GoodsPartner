package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.goodspartner.dto.ProductMeasureDetails;
import lombok.*;

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
    @JsonAlias("Количество")
    private Double totalProductWeight;
    @JsonAlias("Коэффициент")
    private Double coefficient;
    @JsonAlias("Сумма")
    private Double priceAmount;
    @JsonAlias("СуммаНДС")
    private Double priceAmountPDV;
    @JsonAlias("Цена")
    private Double price;
    @JsonAlias("КоличествоМест")
    private int amount;

    private String productName;
    private String productRefKey;
    private String productGTDRefKey;
    private String measure;

    private String qualityUrl;
    private ProductMeasureDetails productUnit;
    private ProductMeasureDetails productPackaging;

    @JsonProperty("Номенклатура")
    private void mapProductName(Map<String, Object> value) {
        this.productName = (String) value.get("Description");
        this.productRefKey = (String) value.get("Ref_Key");
        this.productGTDRefKey = (String) value.get("НоменклатураГТД");

        Map<String, Object> productUnit = (Map<String, Object>) value.get("ЕдиницаХраненияОстатков");
        Map<String, Object> productPackaging = (Map<String, Object>) value.get("ЕдиницаИзмеренияМест");

        this.productUnit = getProductMeasureDetails(productUnit);
        this.productPackaging = productPackaging != null
                ? getProductMeasureDetails(productPackaging)
                : getProductMeasureDetails(productUnit);
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

    private String uktzedCode;

    private ProductMeasureDetails getProductMeasureDetails(Map<String, Object> measureMap) {
        return ProductMeasureDetails.builder()
                .measureStandard((String) measureMap.get("Description"))
                .coefficientStandard(Double.valueOf(String.valueOf(measureMap.get("Коэффициент"))))
                .build();
    }
}
