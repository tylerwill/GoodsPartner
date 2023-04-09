package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.goodspartner.dto.InvoiceProduct;
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
public class ODataInvoiceDto {
    @JsonAlias("Ref_Key")
    private String refKey;
    @JsonAlias("DeletionMark")
    private Boolean deletionMark;
    @JsonAlias("Number")
    private String number;
    @JsonAlias("Date")
    private LocalDate documentDate;
    @JsonAlias("Posted")
    private Boolean posted;
    @JsonAlias("СуммаДокумента")
    private Double invoiceAmount;
    @JsonAlias("АдресДоставки")
    private String deliveryAddress;
    @JsonAlias("Сделка")
    private String orderRefKey;
    @JsonAlias("Организация_Key")
    private String organisationRefKey;

    private List<InvoiceProduct> products;

    private String companyName;
    private String companyAccount;
    private String clientName;
    private String storeAddress;
    private String clientContract;
    private String managerFullName;
    private String bankName;
    private String mfoCode;
    private ODataOrderDto order;
    private ODataOrganisationCodesDto organisationCodes;
    private String phone;
    private String address;
    private Boolean buhBaseProperty;

    @JsonProperty("Организация")
    private void mapCompanyName(Map<String, String> value) {
        this.companyName = value.get("НаименованиеПолное");
    }

    @JsonProperty("БанковскийСчетОрганизации")
    private void mapAccount(Map<String, Object> value) {
        this.companyAccount = (String) value.get("НомерСчета");
        Map<String, String> bank = (Map<String, String>) value.get("Банк");
        this.bankName = bank.get("Description");
        this.mfoCode = bank.get("Code");
    }

    @JsonProperty("Контрагент")
    private void mapClientName(Map<String, String> value) {
        this.clientName = value.get("НаименованиеПолное");
    }

    @JsonProperty("Склад")
    private void mapStore(Map<String, String> value) {
        this.storeAddress = value.get("Местонахождение");
    }

    @JsonProperty("ДоговорКонтрагента")
    private void mapContract(Map<String, String> value) {
        this.clientContract = value.get("Description");
    }

    @JsonProperty("Ответственный")
    private void mapManager(Map<String, Object> value) {
        Map<String, String> person = (Map<String, String>) value.get("ФизЛицо");
        if ( person != null ) {
            this.managerFullName = person.get("Description");
        }
    }
}
