package com.goodspartner.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsmGeocodingDto {

    @JsonIgnore
    private String type;

    @JsonIgnore
    private String licence;

    private Object[] features;
}