package com.goodspartner.service.document;

import com.goodspartner.service.dto.DocumentContent;

import java.util.List;

public interface FileCompiler {
    String getCompiledPdfFile(List<DocumentContent> pdfDocumentDtos);
}