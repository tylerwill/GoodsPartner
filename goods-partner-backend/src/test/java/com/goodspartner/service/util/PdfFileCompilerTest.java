package com.goodspartner.service.util;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.service.dto.PdfDocumentDto;
import com.lowagie.text.pdf.PdfReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfFileCompilerTest extends AbstractBaseDocumentPdfTest {
    private static final String PDF_EXPECTED = "documents/expected-pdf-file.pdf";

    private final PdfFileCompiler compilerSut = new PdfFileCompiler();

    @Test
    void shouldReturnPdfFileAsOutputStreamWhenListOfDocumentDtoProvided() throws IOException {
        PdfReader expectedPdfFile = new PdfReader(PDF_EXPECTED);

        List<PdfDocumentDto> documentDtoList = createPdfDocumentDtos();

        ByteArrayOutputStream resultOutputStream = (ByteArrayOutputStream) compilerSut.getInstance().getCompiledPdfFile(documentDtoList);

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
        String expectedMessage = "The List of PdfDocumentDto cannot be null";
        Throwable resultException = assertThrows(RuntimeException.class,
                () -> compilerSut.getInstance().getCompiledPdfFile(null));

        assertEquals(expectedMessage, resultException.getMessage());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenPdfDocumentDtoListIsEmpty() {
        String expectedMessage = "The List of PdfDocumentDto cannot be empty";
        Throwable resultException = assertThrows(RuntimeException.class,
                () -> compilerSut.getInstance().getCompiledPdfFile(new ArrayList<>()));

        assertEquals(expectedMessage, resultException.getMessage());
    }

    @Test
    @Disabled
    void createExpectedPdfFile() {
        List<PdfDocumentDto> documentDtoList = createPdfDocumentDtos();

        ByteArrayOutputStream resultOutputStream = (ByteArrayOutputStream) compilerSut.getCompiledPdfFile(documentDtoList);
        writeExpectedFile(resultOutputStream.toByteArray(), PDF_EXPECTED);
    }

    private List<PdfDocumentDto> createPdfDocumentDtos() {
        HtmlAggregator generator = new HtmlAggregator();
        InvoiceDto invoiceDto = createInvoiceDto(createProductsWithQualityDocuments());
        String resultBillHtml = generator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_BILL);
        String resultInvoiceHtml = generator.getEnrichedHtml(invoiceDto, HTML_TEMPLATE_INVOICE);

        return List.of(createPdfDocumentDto(resultBillHtml, resultInvoiceHtml),
                createPdfDocumentDto(resultBillHtml, resultInvoiceHtml));
    }
}