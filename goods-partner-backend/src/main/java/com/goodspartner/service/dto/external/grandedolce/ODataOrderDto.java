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
    @JsonAlias("ДатаОтгрузки")
    private LocalDate shippingDate;
    @JsonAlias("Date")
    private LocalDate creationDate; // TODO not mapping so far. Clarify if required
    @JsonAlias("АдресДоставки")
    private String address;
    @JsonAlias("Комментарий")
    private String comment;
    @JsonAlias("DeletionMark")
    private Boolean deletionMark;

    private String clientName;
    private String managerFullName;
    //Enrichment
    private List<Product> products;
    private double orderWeight;
    private boolean excluded;
    private String excludeReason;

    @JsonProperty("Контрагент")
    private void mapClientName(Map<String, String> value) {
        this.clientName = value != null ? value.get("Description") : "Не вказано";
    }

    @JsonProperty("Ответственный")
    private void mapManager(Map<String, String> value) {
        this.managerFullName = value != null ? value.get("Code") : "Не вказано";
    }
}
