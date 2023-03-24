package com.goodspartner.service.dto;

import java.util.Set;

public interface DocumentContent {
    Set<String> getDocument(DocumentPdfType documentPdfType);
}