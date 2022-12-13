package com.goodspartner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DocumentCellInfoDto {
    private String value;
    private CellStyles style;
}
