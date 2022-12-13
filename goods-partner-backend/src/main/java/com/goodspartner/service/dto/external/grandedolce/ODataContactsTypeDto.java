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
public class ODataContactsTypeDto {
    @JsonAlias("Ref_Key")
    private String refKey;
    @JsonAlias("Description")
    private String description;
}
