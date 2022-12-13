package com.goodspartner.service.dto.external.grandedolce;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ODataOrganisationCodesDto {
    @JsonAlias("Организация_Key")
    private String organisationRefKey;
    @JsonAlias("КодПоЕДРПОУ")
    private String edrpouCode;
    @JsonAlias("ИНН")
    private String innCode;
    @JsonAlias("НомерСвидетельства")
    private String organisationNumberCode;
}
