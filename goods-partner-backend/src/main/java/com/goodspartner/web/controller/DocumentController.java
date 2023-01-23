package com.goodspartner.web.controller;

import com.goodspartner.service.document.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(path = "/api/v1/document")
public class DocumentController {
    private static final String ATTACHMENT = "attachment";
    private static final String INVOICE_ZIP_FILENAME = "invoices_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String BILL_ZIP_FILENAME = "bills_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String QUALITY_ZIP_FILENAME = "quality_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String HEADER_DELIMITER = "; ";
    private static final String FILENAME_DELIMITER = "_";
    private static final String HEADER_FILENAME_KEY = "filename=";
    private static final String ARCHIVE_EXTENSION = ".zip";
    @Autowired
    @Qualifier("invoiceDocumentService")
    private DocumentService invoiceService;

    @Autowired
    @Qualifier("billDocumentService")
    private DocumentService billService;

    @Autowired
    @Qualifier("qualityDocumentService")
    private DocumentService qualityService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/invoice/by-route-point/{routePointId}")
    @ApiOperation(value = "Save invoice based on routePoint id",
            notes = "Return invoice",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveInvoiceByRoutePointId(@ApiParam(value = "Need routePoint id to get invoices", required = true)
                                                                                         @PathVariable String routePointId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ATTACHMENT +
                                HEADER_DELIMITER +
                                HEADER_FILENAME_KEY +
                                routePointId + FILENAME_DELIMITER + INVOICE_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponseByRoutPointId(out, Long.parseLong(routePointId), invoiceService));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/invoice/by-route/{routeId}", produces = "application/zip")
    @ApiOperation(value = "Save invoices based on route id",
            notes = "Return invoices in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveInvoicesByOrderRefKeyList(@ApiParam(value = "Need order ref_key list to get invoices", required = true)
                                                                                             @PathVariable String routeId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ATTACHMENT +
                                HEADER_DELIMITER +
                                HEADER_FILENAME_KEY +
                                routeId + FILENAME_DELIMITER + INVOICE_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponseByRoutId(out, Long.parseLong(routeId), invoiceService));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/bill/by-route-point/{routePointId}")
    @ApiOperation(value = "Save bill based on routePoint id",
            notes = "Return bill",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveBillByOrderRefKey(@ApiParam(value = "Need order ref_key list to get invoices", required = true)
                                                                                     @PathVariable String routePointId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ATTACHMENT +
                                HEADER_DELIMITER +
                                HEADER_FILENAME_KEY +
                                routePointId + FILENAME_DELIMITER + BILL_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponseByRoutPointId(out, Long.parseLong(routePointId), billService));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/bill/by-route/{routeId}", produces = "application/zip")
    @ApiOperation(value = "Save bills based on route id",
            notes = "Return bills in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveBillsByOrderRefKeyList(@ApiParam(value = "Need order ref_key list to get bills", required = true)
                                                                                          @PathVariable String routeId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + BILL_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponseByRoutId(out, Long.parseLong(routeId), billService));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/quality/by-route/{routeId}", produces = "application/zip")
    @ApiOperation(value = "Save quality documents based on route id",
            notes = "Return quality documents in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveQualityDocumentsByOrderRefKeyList(@ApiParam(value = "Need order ref_key list to get quality documents", required = true)
                                                                                                     @PathVariable String routeId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + QUALITY_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponseByRoutId(out, Long.parseLong(routeId), qualityService));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/quality/by-route-point/{routePointId}", produces = "application/zip")
    @ApiOperation(value = "Save quality documents based on order ref_key",
            notes = "Return quality documents in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveQualityDocumentsByOrderRefKey(@ApiParam(value = "Need order ref_key list to get quality documents", required = true)
                                                                                                 @PathVariable String routePointId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + QUALITY_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponseByRoutPointId(out, Long.parseLong(routePointId), qualityService));
    }

    private void writeZipToResponseByRoutId(OutputStream out, Long routeId, DocumentService documentService) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            documentService.saveDocumentsByRouteId(zipOutputStream, routeId);
        }
    }

    private void writeZipToResponseByRoutPointId(OutputStream out, Long routePointId, DocumentService documentService) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            documentService.saveDocumentByRoutePointId(zipOutputStream, routePointId);
        }
    }
}
