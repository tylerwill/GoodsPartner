package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ODataProductDto {

    @JsonAlias("Ref_Key")
    private String refKey;
    @JsonAlias("КоличествоМест")
    private int amount;
    @JsonAlias("Количество")
    private double totalProductWeight;
    @JsonAlias("Коэффициент")
    private double coefficient;

    private String productName;
    private String storeName;
    private String measure;
    private double unitWeight;

    @JsonProperty("Номенклатура")
    private void mapProductName(Map<String, String> value) {
        this.productName = value.get("Description");
    }

    @JsonProperty("ЕдиницаИзмерения")
    private void mapMeasurer(Map<String, String> value) {
        String unit = value.get("Description");
        this.measure = unit != null ? unit : "";
    }
}
