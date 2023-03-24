package com.goodspartner.service.util;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.service.document.impl.DefaultHtmlAggregator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultHtmlAggregatorTest extends AbstractBaseDocumentPdfTest {

    protected DefaultHtmlAggregator generatorSut = new DefaultHtmlAggregator();

    @Test
    void shouldReturnHtmlAsStringWithInsertedDataOfBill() {
        String expectedHtml = getExpectedHtml(BILL_EXPECTED);

        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        String resultHtml = generatorSut.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_BILL);

        assertFalse(resultHtml.isEmpty());
        assertEquals(expectedHtml, resultHtml);
    }

    @Test
    void shouldReturnHtmlAsStringWithInsertedDataOfInvoice() {
        String expectedHtml = getExpectedHtml(INVOICE_EXPECTED);

        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        String resultHtml = generatorSut.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_INVOICE);

        assertFalse(resultHtml.isEmpty());
        assertEquals(expectedHtml, resultHtml);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenContentIsNull() {
        String expectedExceptionMessage = "The Content for Html template is null";

        Throwable resultException = assertThrows(RuntimeException.class, () -> generatorSut.getEnrichedHtml(null, HTML_TEMPLATE_BILL));
        String resultExceptionMessage = resultException.getMessage();

        assertEquals(expectedExceptionMessage, resultExceptionMessage);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenHtmlTemplateNameIsNull() {
        String expectedExceptionMessage = "The Html template name is null or empty";
        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        Throwable resultException = assertThrows(RuntimeException.class, () -> generatorSut.getEnrichedHtml(invoiceDto, null));
        String resultExceptionMessage = resultException.getMessage();

        assertEquals(expectedExceptionMessage, resultExceptionMessage);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenHtmlTemplateNameIsEmpty() {
        String expectedExceptionMessage = "The Html template name is null or empty";
        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        Throwable resultException = assertThrows(RuntimeException.class, () -> generatorSut.getEnrichedHtml(invoiceDto, ""));
        String resultExceptionMessage = resultException.getMessage();

        assertEquals(expectedExceptionMessage, resultExceptionMessage);
    }

    @Test
    @Disabled
    void createExpectedBillAndInvoiceTemplates() {
        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());

        String resultBillHtml = generatorSut.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_BILL);
        String resultInvoiceHtml = generatorSut.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_INVOICE);

        writeExpectedFile(resultBillHtml.getBytes(), BILL_EXPECTED);
        writeExpectedFile(resultInvoiceHtml.getBytes(), INVOICE_EXPECTED);
    }
}
