package com.goodspartner.service.util;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.service.dto.DocumentPdfType;
import com.goodspartner.service.dto.PdfDocumentDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PdfDocumentDtoGeneratorTest extends AbstractBaseDocumentPdfTest {

    PdfDocumentDtoGenerator pdfGeneratorSut = new PdfDocumentDtoGenerator();

    @Test
    void shouldReturnPdfDocumentDtoWithQualityDocuments() {
        String expectedBill = getExpectedHtml(BILL_EXPECTED);
        String expectedInvoice = getExpectedHtml(INVOICE_EXPECTED);

        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        PdfDocumentDto pdfDocumentDto = pdfGeneratorSut.getPdfDocumentDto(invoiceDto);

        String resultBill = pdfDocumentDto.getBill();
        String resultInvoice = pdfDocumentDto.getInvoice();
        var resultQualityDocs = pdfDocumentDto.getQualityDocuments();

        assertNotNull(resultBill);
        assertFalse(resultBill.isEmpty());
        assertEquals(expectedBill, resultBill);
        assertNotNull(resultInvoice);
        assertFalse(resultInvoice.isEmpty());
        assertEquals(expectedInvoice, resultInvoice);
        assertEquals(resultQualityDocs.get(DocumentPdfType.IMAGES).size(), 2);
        assertEquals(resultQualityDocs.get(DocumentPdfType.PDF).size(), 1);
    }

    @Test
    void shouldReturnPdfDocumentDtoQualityDocumentsAreAbsent() {
        String expectedBill = getExpectedHtml(BILL_EXPECTED);
        String expectedInvoice = getExpectedHtml(INVOICE_EXPECTED);

        InvoiceDto invoiceDto = createInvoiceDto(createProductsQualityDocumentsAreAbsent());

        PdfDocumentDto pdfDocumentDto = pdfGeneratorSut.getPdfDocumentDto(invoiceDto);

        String resultBill = pdfDocumentDto.getBill();
        String resultInvoice = pdfDocumentDto.getInvoice();
        var resultQualityDocs = pdfDocumentDto.getQualityDocuments();

        assertNotNull(resultBill);
        assertFalse(resultBill.isEmpty());
        assertEquals(expectedBill, resultBill);
        assertNotNull(resultInvoice);
        assertFalse(resultInvoice.isEmpty());
        assertEquals(expectedInvoice, resultInvoice);
        assertEquals(resultQualityDocs.get(DocumentPdfType.IMAGES).size(), 0);
        assertEquals(resultQualityDocs.get(DocumentPdfType.PDF).size(), 0);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenInvoiceDtoIsNull() {
        assertThrows(RuntimeException.class, () -> pdfGeneratorSut.getPdfDocumentDto(null));
    }
}
