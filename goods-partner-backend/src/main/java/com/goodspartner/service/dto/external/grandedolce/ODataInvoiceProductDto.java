package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String productName;
    private String productRefKey;
    private String productGTDRefKey;
    private String measure;

    private String qualityUrl;

    @JsonProperty("Номенклатура")
    private void mapProductName(Map<String, String> value) {
        this.productName = value.get("Description");
        this.productRefKey = value.get("Ref_Key");
        this.productGTDRefKey = value.get("НоменклатураГТД");
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
}
