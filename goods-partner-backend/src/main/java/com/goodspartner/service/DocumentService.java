package com.goodspartner.service;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.report.ReportResult;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface DocumentService {
    void saveDocumentsByOrderRefKeys(ZipOutputStream zipOutputStream, List<String> orderRefKeys);
    ReportResult saveDocumentByOrderRefKey(String orderRefKey);
}
