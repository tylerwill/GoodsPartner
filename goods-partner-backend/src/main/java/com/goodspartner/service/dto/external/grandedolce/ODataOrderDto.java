package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.goodspartner.dto.ProductDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
// common
public class ODataOrderDto {

    @JsonAlias("Ref_Key")
    private String refKey;
    @JsonAlias("Number")
    private String orderNumber;
    @JsonAlias("Date")
    private LocalDate createdDate;
    @JsonAlias("АдресДоставки")
    private String address;
    @JsonAlias("Комментарий")
    private String comment;

    private String clientName;
    private String managerFullName;

    //Enrichment
    private List<ProductDto> products;
    private double orderWeight;

    @JsonProperty("Контрагент")
    private void mapClientName(Map<String, String> value) {
        this.clientName = value.get("Description");
    }

    @JsonProperty("Ответственный")
    private void mapManager(Map<String, String> value) {
        this.managerFullName = value.get("Code");
    }
}
