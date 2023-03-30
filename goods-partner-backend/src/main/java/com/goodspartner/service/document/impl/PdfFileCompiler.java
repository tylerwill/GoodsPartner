package com.goodspartner.service.document.impl;

import com.goodspartner.service.document.FileCompiler;
import com.goodspartner.service.document.FileFetcher;
import com.goodspartner.service.dto.DocumentContent;
import com.goodspartner.service.dto.DocumentPdfType;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class PdfFileCompiler implements FileCompiler {

    private static final String FONT_ARIAL = "documents/fonts/arial.ttf";
    private final static int NUMBER_OF_COPIES = 2;
    private final static int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private final static int FIRST_PAGE = 1;

    private final Document resultPdfFile;
    private final ByteArrayOutputStream pdfFileHost;
    private ITextRenderer renderer;
    private final PdfCopy pdfCopier;
    private final FileFetcher fileFetcher;
    private List<DocumentContent> pdfDocumentContents;

    public PdfFileCompiler(FileFetcher fileFetcher) {
        this(new ITextRenderer(),
                new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE),
                new Document(PageSize.A4),
                fileFetcher);
    }

    public PdfFileCompiler(ITextRenderer renderer,
                           ByteArrayOutputStream pdfFileHost,
                           Document resultPdfFile,
                           FileFetcher fileFetcher) {
        this.renderer = renderer;
        this.pdfFileHost = pdfFileHost;
        this.resultPdfFile = resultPdfFile;
        pdfCopier = new PdfCopy(resultPdfFile, pdfFileHost);
        this.fileFetcher = fileFetcher;
    }

    public OutputStream getCompiledPdfFile(List<DocumentContent> pdfDocumentContents) {
        try {
            setPdfDocumentDtos(pdfDocumentContents);
            insertToResultPdfFile();

            return pdfFileHost;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void setPdfDocumentDtos(List<DocumentContent> pdfDocumentContents) {
        requiredNotNull(pdfDocumentContents);
        requiredNotEmpty(pdfDocumentContents);
        this.pdfDocumentContents = pdfDocumentContents;
    }

    private void requiredNotNull(List<DocumentContent> pdfDocumentContents) {
        if (Objects.isNull(pdfDocumentContents)) {
            throw new RuntimeException("The List of PdfDocumentContent cannot be null");
        }
    }

    private void requiredNotEmpty(List<DocumentContent> pdfDocumentContents) {
        if (pdfDocumentContents.isEmpty()) {
            throw new RuntimeException("The List of PdfDocumentContent cannot be empty");
        }
    }

    private void insertToResultPdfFile() throws IOException {
        resultPdfFile.open();

        for (DocumentContent pdfDocumentContent : pdfDocumentContents) {
            insertBillAndInvoicesAndImagesQualityDocuments(pdfDocumentContent);
            insertPdfQualityDocuments(pdfDocumentContent);
        }

        resultPdfFile.close();
    }

    private void insertBillAndInvoicesAndImagesQualityDocuments(DocumentContent pdfDocumentContent) throws IOException {
        byte[] pdfOfBillAndInvoicesAndImagesQualityDocuments =
                getPdfOfBillAndInvoicesAndImagesQualityDocuments(pdfDocumentContent);

        copyPdfPages(pdfOfBillAndInvoicesAndImagesQualityDocuments);
    }

    private byte[] getPdfOfBillAndInvoicesAndImagesQualityDocuments(DocumentContent pdfDocumentContent) throws IOException {
        renderer = new ITextRenderer();
        ByteArrayOutputStream pdfHost = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
        setFontArial();

        insertBill(pdfDocumentContent, pdfHost);
        insertInvoiceTwice(pdfDocumentContent);
        insertImagesQualityDocuments(pdfDocumentContent);

        renderer.finishPDF();

        return pdfHost.toByteArray();
    }

    private void setFontArial() throws IOException {
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont(FONT_ARIAL, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private void insertBill(DocumentContent pdfDocumentContent, OutputStream outputStream) {
        String bill = getDocument(pdfDocumentContent, DocumentPdfType.BILL);
        bill = bill.replaceAll("& ", "&amp; ");
        renderer.setDocumentFromString(bill);
        renderer.layout();
        renderer.createPDF(outputStream, false);
    }

    private void insertInvoiceTwice(DocumentContent pdfDocumentContent) {
        String htmlInvoice = getDocument(pdfDocumentContent, DocumentPdfType.INVOICE);
        htmlInvoice = htmlInvoice.replaceAll("& ", "&amp; ");
        for (int i = 0; i < NUMBER_OF_COPIES; i++) {
            writeToPdfDocument(htmlInvoice);
        }
    }

    private String getDocument(DocumentContent documentContent, DocumentPdfType documentPdfType) {
        Set<String> documents = documentContent.getDocument(documentPdfType);
        Iterator<String> stringIterator = documents.iterator();
        return stringIterator.next();
    }

    private void writeToPdfDocument(String docAsString) {
        renderer.setDocumentFromString(docAsString);
        renderer.layout();
        renderer.writeNextDocument();
    }

    private void insertImagesQualityDocuments(DocumentContent pdfDocumentContent) {
        Set<String> qualityDocImages = pdfDocumentContent.getDocument(DocumentPdfType.IMAGES);

        if (qualityDocImages.isEmpty()) {
            return;
        }

        qualityDocImages.stream()
                .map(this::openSourceFile)
                .filter(Objects::nonNull)
                .map(this::getImage)
                .forEach(this::writeToPdfDocument);
    }

    private byte[] openSourceFile(String fileUrl) {
        try {
            return openSourceFileFromExternalHost(fileUrl);
        } catch (IOException e) {
            return openSourceFileFromLocalHost(fileUrl);
        }
    }

    private byte[] openSourceFileFromExternalHost(String fileUrl) throws IOException {
        String updatedUrl = fileFetcher.updateUrl(fileUrl);
        try {
            return fileFetcher.getFileThroughInternet(updatedUrl);
        } catch (Exception e) {
            log.warn("There was a problem with a Quality document {} at external host: {}", updatedUrl, e.getMessage());
            throw new IOException(e);
        }
    }

    private byte[] openSourceFileFromLocalHost(String fileUrl) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileUrl)) {
            Objects.requireNonNull(inputStream);
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.warn("There was a problem with a Quality document {} at local host: {}", fileUrl, e.getMessage());
            return null;
        }
    }

    private String getImage(byte[] image) {
        return "<html>\n" +
                "    <body>\n" +
                " <div><img width=\"655\" src=\"data:image/png;base64," + Base64.getEncoder().encodeToString(image) + "\"></img></div>\n" +
                "    </body>\n" +
                "</html>";
    }

    private void insertPdfQualityDocuments(DocumentContent pdfDocumentContent) {
        Set<String> pdfQualityDocumentUrls = pdfDocumentContent.getDocument(DocumentPdfType.PDF);

        if (pdfQualityDocumentUrls.isEmpty()) {
            return;
        }

        pdfQualityDocumentUrls.stream()
                .map(this::openSourceFile)
                .filter(Objects::nonNull)
                .forEach(this::copyPdfPages);
    }

    private void copyPdfPages(byte[] filePdf) {
        try (PdfReader pdfReader = new PdfReader(filePdf)) {
            int numberOfPages = pdfReader.getNumberOfPages();
            for (int page = FIRST_PAGE; page <= numberOfPages; page++) {
                PdfImportedPage pdfImportedPage = pdfCopier.getImportedPage(pdfReader, page);
                pdfCopier.addPage(pdfImportedPage);
            }
        } catch (Exception e) {
            log.warn("A problem occurred when PDF Quality documents were copied: {}", e.getMessage());
        }
    }
}
