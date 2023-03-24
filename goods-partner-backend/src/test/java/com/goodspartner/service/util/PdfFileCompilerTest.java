package com.goodspartner.service.util;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.service.document.impl.DefaultHtmlAggregator;
import com.goodspartner.service.document.impl.DefaultFileFetcher;
import com.goodspartner.service.document.FileFetcher;
import com.goodspartner.service.document.impl.PdfFileCompiler;
import com.goodspartner.service.dto.DocumentContent;
import com.lowagie.text.pdf.PdfReader;
import org.junit.jupiter.api.*;
import org.junit.runner.OrderWith;

import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfFileCompilerTest extends AbstractBaseDocumentPdfTest {
    private static final String PDF_EXPECTED = "documents/expected-pdf-file.pdf";
    private final FileFetcher connector = new DefaultFileFetcher(new ClientProperties(), WebClient.builder().build());
    private final PdfFileCompiler compilerSut = new PdfFileCompiler(connector);

    @Test
    @Disabled
    void shouldReturnPdfFileAsOutputStreamWhenListOfDocumentDtoProvided() throws IOException {
        PdfReader expectedPdfFile = new PdfReader(PDF_EXPECTED);

        List<DocumentContent> documentContentList = createPdfDocumentDtos();

        ByteArrayOutputStream resultOutputStream = (ByteArrayOutputStream) compilerSut.getCompiledPdfFile(documentContentList);

        PdfReader resultPdfFile = new PdfReader(resultOutputStream.toByteArray());

        assertEquals(expectedPdfFile.getNumberOfPages(), resultPdfFile.getNumberOfPages());
        assertArrayEquals(expectedPdfFile.getPageContent(1), resultPdfFile.getPageContent(1));
        assertArrayEquals(expectedPdfFile.getPageContent(2), resultPdfFile.getPageContent(2));
        assertArrayEquals(expectedPdfFile.getPageContent(3), resultPdfFile.getPageContent(3));
        assertArrayEquals(expectedPdfFile.getPageContent(4), resultPdfFile.getPageContent(4));
        assertArrayEquals(expectedPdfFile.getPageContent(5), resultPdfFile.getPageContent(5));
        assertArrayEquals(expectedPdfFile.getPageContent(6), resultPdfFile.getPageContent(6));
        assertArrayEquals(expectedPdfFile.getPageContent(7), resultPdfFile.getPageContent(7));
        assertArrayEquals(expectedPdfFile.getPageContent(8), resultPdfFile.getPageContent(8));
        assertArrayEquals(expectedPdfFile.getPageContent(9), resultPdfFile.getPageContent(9));

        expectedPdfFile.close();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenPdfDocumentDtoListIsNull() {
        String expectedMessage = "The List of PdfDocumentContent cannot be null";
        Throwable resultException = assertThrows(RuntimeException.class,
                () -> compilerSut.getCompiledPdfFile(null));

        assertEquals(expectedMessage, resultException.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenPdfDocumentDtoListIsEmpty() {
        String expectedMessage = "The List of PdfDocumentContent cannot be empty";
        Throwable resultException = assertThrows(RuntimeException.class,
                () -> compilerSut.getCompiledPdfFile(new ArrayList<>()));

        assertEquals(expectedMessage, resultException.getMessage());
    }

    @Test
    @Disabled
    void createExpectedPdfFile() {
        List<DocumentContent> documentContentList = createPdfDocumentDtos();

        ByteArrayOutputStream resultOutputStream = (ByteArrayOutputStream) compilerSut.getCompiledPdfFile(documentContentList);
        writeExpectedFile(resultOutputStream.toByteArray(), PDF_EXPECTED);
    }

    private List<DocumentContent> createPdfDocumentDtos() {
        DefaultHtmlAggregator generator = new DefaultHtmlAggregator();
        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());
        String resultBillHtml = generator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_BILL);
        String resultInvoiceHtml = generator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_INVOICE);

        return List.of(createPdfDocumentDto(resultBillHtml, resultInvoiceHtml),
                createPdfDocumentDto(resultBillHtml, resultInvoiceHtml));
    }
}