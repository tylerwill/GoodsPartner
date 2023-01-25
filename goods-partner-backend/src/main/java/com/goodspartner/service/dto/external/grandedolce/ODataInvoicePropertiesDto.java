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
public class ODataInvoicePropertiesDto {
    @JsonAlias("Объект")
    private String invoiceRefKey;
    @JsonAlias("Свойство_Key")
    private String propertyRefKey;

    private Boolean propertyValue;
    private String propertyName;

    @JsonProperty("Свойство")
    private void mapPropertyName(Map<String, String> value) {
        this.propertyName = value.get("Description");
    }

    @JsonProperty("Значение")
    private void mapValue(String value){
        this.propertyValue = Boolean.parseBoolean(value);
    }
}
