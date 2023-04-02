package com.goodspartner.web.controller;

import com.goodspartner.entity.DeliveryType;
import com.goodspartner.service.document.DocumentPdfService;
import com.goodspartner.service.dto.DocumentDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/documents")
public class DocumentPdfController {
    private static final String ATTACHMENT = "attachment";
    private static final String ORDER = "order_";
    private static final String FILENAME_DELIMITER = "_";
    private static final String FILE_EXTENSION = ".pdf";
    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;


    private final DocumentPdfService pdfService;

    public DocumentPdfController(DocumentPdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(params = "routeId", produces = "application/pdf")
    @ApiOperation(value = "Get a compiled PDF file based on Route id",
            notes = "Return a compiled PDF file",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> pdfDocumentsByRoute(@ApiParam(value = "Need Route id to get PDF documents", required = true)
                                                                                   @RequestParam String routeId) {
        DocumentDto documentDto = pdfService.getPdfDocumentsByRoute(Long.parseLong(routeId));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, getFileNameByRoute(documentDto))
                .body(outputStream -> uploadPdf(documentDto, outputStream));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(params = "routePointId", produces = "application/pdf")
    @ApiOperation(value = "Get a compiled PDF file based on routePoint id",
            notes = "Return a compiled PDF file",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> pdfDocumentsByRoutePoint(@ApiParam(value = "Need routePoint id to get PDF documents", required = true)
                                                                                        @RequestParam String routePointId) {
        DocumentDto documentDto = pdfService.getPdfDocumentsByRoutePoint(Long.parseLong(routePointId));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, getFileNameByRoutePoint(documentDto))
                .body(outputStream -> uploadPdf(documentDto, outputStream));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(params = "deliveryId", produces = "application/pdf")
    @ApiOperation(value = "Get a compiled PDF file based on delivery id and delivery type",
            notes = "Return a compiled PDF file",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> pdfDocumentsByDeliveryIdAndDeliveryType(@RequestParam UUID deliveryId,
                                                                                                       @RequestParam DeliveryType deliveryType) {
        DocumentDto documentDto = pdfService.getPdfDocumentsByDeliveryId(deliveryId, deliveryType);
        documentDto.setCarName(deliveryType.getName());
        documentDto.setCarLicencePlate(StringUtils.EMPTY);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, getFileNameByRoutePoint(documentDto))
                .body(outputStream -> uploadPdf(documentDto, outputStream));
    }

    private String getFileNameByRoute(DocumentDto documentDto) {
        String fileName = getFileName(documentDto);
        return adaptFileName(fileName);
    }

    private String getFileNameByRoutePoint(DocumentDto documentDto) {
        String fileNamePrefix = getOrderNumberPrefix(documentDto);
        String fileNameSuffix = getFileName(documentDto);
        return adaptFileName(fileNamePrefix + fileNameSuffix);
    }

    private String getFileName(DocumentDto documentDto) {
        return documentDto.getDeliveryDate() + FILENAME_DELIMITER +
                documentDto.getCarName() + FILENAME_DELIMITER +
                documentDto.getCarLicencePlate() + FILE_EXTENSION;
    }
    private String getOrderNumberPrefix(DocumentDto documentDto) {
        return ORDER + documentDto.getOrderNumber() + FILENAME_DELIMITER;
    }

    private String adaptFileName(String fileName) {
        return ContentDisposition
                .builder(ATTACHMENT)
                .filename(fileName, StandardCharsets.UTF_8)
                .build()
                .toString();
    }
    private void uploadPdf(DocumentDto documentDto, OutputStream outputStream) throws IOException {
        File file = new File(documentDto.getDocumentContent());
        InputStream inputStream = new FileInputStream(file);
        int nRead;
        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            outputStream.write(data, 0, nRead);
        }
        inputStream.close();
    }
}
