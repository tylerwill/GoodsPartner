package com.goodspartner.service.util;

import com.goodspartner.service.dto.DocumentPdfType;
import com.goodspartner.service.dto.PdfDocumentDto;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class PdfFileCompiler {

    private static final String FONT_ARIAL = "documents/fonts/arial.ttf";
    private final static int NUMBER_OF_COPIES = 2;
    private final static int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private final static int FIRST_PAGE = 1;

    private final Document resultPdfFile;
    private final ByteArrayOutputStream pdfFileHost;
    private ITextRenderer renderer;
    private final PdfCopy pdfCopier;

    private List<PdfDocumentDto> pdfDocumentDtos;

    public PdfFileCompiler() {
        this(new ITextRenderer(),
                new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE),
                new Document(PageSize.A4));
    }

    public PdfFileCompiler(ITextRenderer renderer,
                           ByteArrayOutputStream pdfFileHost,
                           Document resultPdfFile) {
        this.renderer = renderer;
        this.pdfFileHost = pdfFileHost;
        this.resultPdfFile = resultPdfFile;
        pdfCopier = new PdfCopy(resultPdfFile, pdfFileHost);
    }

    public PdfFileCompiler getInstance() {
        return new PdfFileCompiler();
    }

    public OutputStream getCompiledPdfFile(List<PdfDocumentDto> pdfDocumentDtos) {
        try {
            setPdfDocumentDtos(pdfDocumentDtos);

            insertToResultPdfFile();

            return pdfFileHost;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void setPdfDocumentDtos(List<PdfDocumentDto> pdfDocumentDtos) {
        requiredNotNull(pdfDocumentDtos);
        requiredNotEmpty(pdfDocumentDtos);
        this.pdfDocumentDtos = pdfDocumentDtos;
    }

    private void requiredNotNull(List<PdfDocumentDto> pdfDocumentDtos) {
        if (Objects.isNull(pdfDocumentDtos)) {
            throw new RuntimeException("The List of PdfDocumentDto cannot be null");
        }
    }

    private void requiredNotEmpty(List<PdfDocumentDto> pdfDocumentDtos) {
        if (pdfDocumentDtos.isEmpty()) {
            throw new RuntimeException("The List of PdfDocumentDto cannot be empty");
        }
    }

    private void insertToResultPdfFile() throws IOException {
        resultPdfFile.open();

        for (PdfDocumentDto pdfDocumentDto : pdfDocumentDtos) {
            insertBillAndInvoicesAndImagesQualityDocuments(pdfDocumentDto);
            insertPdfQualityDocuments(pdfDocumentDto);
        }

        resultPdfFile.close();
    }

    private void insertBillAndInvoicesAndImagesQualityDocuments(PdfDocumentDto pdfDocumentDto) throws IOException {
        byte[] pdfOfBillAndInvoicesAndImagesQualityDocuments =
                getPdfOfBillAndInvoicesAndImagesQualityDocuments(pdfDocumentDto);

        copyPdfPages(pdfOfBillAndInvoicesAndImagesQualityDocuments);

    }

    private byte[] getPdfOfBillAndInvoicesAndImagesQualityDocuments(PdfDocumentDto pdfDocumentDto) throws IOException {
        renderer = new ITextRenderer();
        ByteArrayOutputStream pdfHost = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
        setFontArial();

        insertBill(pdfDocumentDto, pdfHost);
        insertInvoiceTwice(pdfDocumentDto);
        insertImagesQualityDocuments(pdfDocumentDto);

        renderer.finishPDF();

        return pdfHost.toByteArray();
    }

    private void setFontArial() throws IOException {
        ITextFontResolver fontResolver = renderer.getFontResolver();
        fontResolver.addFont(FONT_ARIAL, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private void insertBill(PdfDocumentDto pdfDocumentDto, OutputStream outputStream) {
        renderer.setDocumentFromString(pdfDocumentDto.getBill());
        renderer.layout();
        renderer.createPDF(outputStream, false);
    }

    private void insertInvoiceTwice(PdfDocumentDto pdfDocumentDto) {
        String htmlInvoice = pdfDocumentDto.getInvoice();
        for (int i = 0; i < NUMBER_OF_COPIES; i++) {
            writeToPdfDocument(htmlInvoice);
        }
    }

    private void writeToPdfDocument(String docAsString) {
//        String preparedImage = getImage(image);
        renderer.setDocumentFromString(docAsString);
        renderer.layout();
        renderer.writeNextDocument();
    }

    private void insertImagesQualityDocuments(PdfDocumentDto pdfDocumentDto) {
        Set<String> qualityDocImages = pdfDocumentDto.getQualityDocuments().get(DocumentPdfType.IMAGES);

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
        try (InputStream fileFromExternalHost = new URL(fileUrl).openStream()) {
            Objects.requireNonNull(fileFromExternalHost);
            return fileFromExternalHost.readAllBytes();
        } catch (IOException e) {
            log.error("There was a problem with a Quality document {} at external host: {}", fileUrl, e.getMessage());
            throw new IOException(e);
        }
    }

    private byte[] openSourceFileFromLocalHost(String fileUrl) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileUrl)) {
            Objects.requireNonNull(inputStream);
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("There was a problem with a Quality document {} at local host: {}", fileUrl, e.getMessage());
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

    private void insertPdfQualityDocuments(PdfDocumentDto pdfDocumentDto) {
        Set<String> pdfQualityDocumentUrls = pdfDocumentDto.getQualityDocuments().get(DocumentPdfType.PDF);

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
            log.error("A problem occurred when PDF Quality documents were copied: {}", e.getMessage());
        }
    }
}
