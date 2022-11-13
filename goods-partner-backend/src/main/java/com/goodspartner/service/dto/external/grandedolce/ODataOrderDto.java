package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.goodspartner.dto.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ODataOrderDto {

    @JsonAlias("Ref_Key")
    private String refKey;
    @JsonAlias("Number")
    private String orderNumber;
    @JsonAlias("Date")
    private LocalDate shippingDate;
    @JsonAlias("АдресДоставки")
    private String address;
    @JsonAlias("Комментарий")
    private String comment;

    private String clientName;
    private String managerFullName;
    //Enrichment
    private List<Product> products;
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
