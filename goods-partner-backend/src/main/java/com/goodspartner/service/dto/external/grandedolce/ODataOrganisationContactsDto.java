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
public class ODataOrganisationContactsDto {
    @JsonAlias("Объект")
    private String organisationRefKey;
    @JsonAlias("Тип")
    private String type;
    @JsonAlias("Вид")
    private String view;
    @JsonAlias("Представление")
    private String contact;
}
