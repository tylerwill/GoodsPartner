package com.goodspartner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
public class PdfDocumentDto {
    private String bill;
    private String invoice;
    private Map<DocumentPdfType, Set<String>> qualityDocuments;
}
