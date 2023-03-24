package com.goodspartner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
public class PdfDocumentContent implements DocumentContent {
    private Map<DocumentPdfType, Set<String>> documents;
    @Override
    public Set<String> getDocument(DocumentPdfType documentPdfType) {
        return documents.get(documentPdfType);
    }
}
