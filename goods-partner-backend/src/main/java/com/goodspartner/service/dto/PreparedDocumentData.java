package com.goodspartner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class PreparedDocumentData {
    private String documentName;
    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> header;
    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> table;
    private Map<Integer, List<Pair<Integer, DocumentCellInfoDto>>> signature;
}
