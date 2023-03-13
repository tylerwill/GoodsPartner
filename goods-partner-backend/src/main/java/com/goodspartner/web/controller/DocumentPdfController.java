package com.goodspartner.web.controller;

import com.goodspartner.service.document.DocumentPdfService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "/api/v1/pdf-document")
public class DocumentPdfController {
    private static final String ATTACHMENT = "attachment; filename=";
    private static final String ROUTE_FILENAME = "route_";
    private static final String ROUTE_POINT_FILENAME = "route-point_";
    private static final String FILENAME_DELIMITER = "_";
    private static final String TIMESTAMP = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String FILE_EXTENSION = ".pdf";

    private final DocumentPdfService pdfService;

    public DocumentPdfController(DocumentPdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/by-route/{routeId}", produces = "application/pdf")
    @ApiOperation(value = "Get a compiled PDF file based on Route id",
            notes = "Return a compiled PDF file",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> pdfDocumentsByRoute(@ApiParam(value = "Need Route id to get PDF documents", required = true)
                                                                                   @PathVariable String routeId) {
        ByteArrayOutputStream compoundPdfFileAccordingRouteId =
                (ByteArrayOutputStream) pdfService.getPdfDocumentsByRoute(Long.parseLong(routeId));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, getFileName(routeId, ROUTE_FILENAME))
                .body(compoundPdfFileAccordingRouteId::writeTo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/by-route-point/{routePointId}", produces = "application/pdf")
    @ApiOperation(value = "Get a compiled PDF file based on routePoint id",
            notes = "Return a compiled PDF file",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> pdfDocumentsByRoutePoint(@ApiParam(value = "Need routePoint id to get PDF documents", required = true)
                                                                                        @PathVariable String routePointId) {
        ByteArrayOutputStream compoundPdfFileAccordingRoutePointId =
                (ByteArrayOutputStream) pdfService.getPdfDocumentsByRoutePoint(Long.parseLong(routePointId));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, getFileName(routePointId, ROUTE_POINT_FILENAME))
                .body(compoundPdfFileAccordingRoutePointId::writeTo);
    }

    private String getFileName(String id, String fileNamePrefix) {
        return ATTACHMENT +
                fileNamePrefix + id + FILENAME_DELIMITER + TIMESTAMP + FILE_EXTENSION;
    }
}
