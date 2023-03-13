package com.goodspartner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DocumentPdfType {
    PDF(new String[]{".pdf"}),
    IMAGES(new String[]{".jpg", ".jpeg", ".png", ".bmp"});

    private final String[] extensions;
}
