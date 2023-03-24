package com.goodspartner.service.document;

import com.goodspartner.dto.InvoiceDto;

public interface HtmlAggregator {
    String getEnrichedHtml(InvoiceDto invoiceDto, String htmlTemplate);
}