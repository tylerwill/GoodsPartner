package com.goodspartner.service.util;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.service.document.DocumentContentGenerator;
import com.goodspartner.service.document.impl.PdfDocumentContentGenerator;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentPdfType;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PdfDocumentContentGeneratorTest extends AbstractBaseDocumentPdfTest {

    DocumentContentGenerator pdfGeneratorSut = new PdfDocumentContentGenerator();

    @Test
    void shouldReturnPdfDocumentDtoWithQualityDocuments() {
        String expectedBill = getExpectedHtml(BILL_EXPECTED);
        String expectedInvoice = getExpectedHtml(INVOICE_EXPECTED);

        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        DocumentContent pdfDocumentContent = pdfGeneratorSut.getDocumentContent(invoiceDto);

        String resultBill = getDocument(pdfDocumentContent, DocumentPdfType.BILL);
        String resultInvoice = getDocument(pdfDocumentContent, DocumentPdfType.INVOICE);
        var resultQualityDocsImage = pdfDocumentContent.getDocument(DocumentPdfType.IMAGES);
        var resultQualityDocsPdf = pdfDocumentContent.getDocument(DocumentPdfType.PDF);

        assertNotNull(resultBill);
        assertFalse(resultBill.isEmpty());
        assertEquals(expectedBill, resultBill);
        assertNotNull(resultInvoice);
        assertFalse(resultInvoice.isEmpty());
        assertEquals(expectedInvoice, resultInvoice);
        assertEquals(resultQualityDocsImage.size(), 2);
        assertEquals(resultQualityDocsPdf.size(), 1);
    }

    @Test
    void shouldReturnPdfDocumentDtoQualityDocumentsAreAbsent() {
        String expectedBill = getExpectedHtml(BILL_EXPECTED);
        String expectedInvoice = getExpectedHtml(INVOICE_EXPECTED);

        InvoiceDto invoiceDto = createInvoiceDto(createProductsQualityDocumentsAreAbsent());

        DocumentContent pdfDocumentContent = pdfGeneratorSut.getDocumentContent(invoiceDto);

        String resultBill = getDocument(pdfDocumentContent, DocumentPdfType.BILL);
        String resultInvoice = getDocument(pdfDocumentContent, DocumentPdfType.INVOICE);
        var resultQualityDocsImage = pdfDocumentContent.getDocument(DocumentPdfType.IMAGES);
        var resultQualityDocsPdf = pdfDocumentContent.getDocument(DocumentPdfType.PDF);

        assertNotNull(resultBill);
        assertFalse(resultBill.isEmpty());
        assertEquals(expectedBill, resultBill);
        assertNotNull(resultInvoice);
        assertFalse(resultInvoice.isEmpty());
        assertEquals(expectedInvoice, resultInvoice);
        assertEquals(resultQualityDocsImage.size(), 0);
        assertEquals(resultQualityDocsPdf.size(), 0);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenInvoiceDtoIsNull() {
        assertThrows(RuntimeException.class, () -> pdfGeneratorSut.getDocumentContent(null));
    }

    private String getDocument(DocumentContent documentContent, DocumentPdfType documentPdfType) {
        Set<String> documents = documentContent.getDocument(documentPdfType);
        Iterator<String> stringIterator = documents.iterator();
        return stringIterator.next();
    }
}
