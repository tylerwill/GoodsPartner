package com.goodspartner.service.document;

import com.goodspartner.service.dto.DocumentContent;

import java.io.OutputStream;
import java.util.List;

public interface FileCompiler {
    OutputStream getCompiledPdfFile(List<DocumentContent> pdfDocumentDtos);
}