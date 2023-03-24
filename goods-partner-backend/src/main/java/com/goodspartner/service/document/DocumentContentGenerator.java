package com.goodspartner.service.document;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.service.dto.DocumentContent;

public interface DocumentContentGenerator {
    DocumentContent getDocumentContent(InvoiceDto invoiceDto);
}