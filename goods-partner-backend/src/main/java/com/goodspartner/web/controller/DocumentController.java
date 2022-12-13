package com.goodspartner.web.controller;

import com.goodspartner.report.ReportResult;
import com.goodspartner.service.DocumentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(path = "/api/v1/document")
public class DocumentController {
    private static final String ATTACHMENT = "attachment";
    private static final String INVOICE_ZIP_FILENAME = "invoices_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String BILL_ZIP_FILENAME = "bills_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String QUALITY_ZIP_FILENAME = "quality_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static final String HEADER_DELIMITER = "; ";
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
    @GetMapping(value = "/save/invoice/{ref}")
    @ApiOperation(value = "Save invoice based on order ref key",
            notes = "Return invoice",
            response = HttpEntity.class)
    public @ResponseBody HttpEntity<byte[]> saveInvoiceByOrderRefKey(@ApiParam(value = "Need order ref_key list to get invoices", required = true)
                                                                     @PathVariable String ref) {
        ReportResult reportResult = invoiceService.saveDocumentByOrderRefKey(ref);
        return toHttpEntity(reportResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/invoice", produces = "application/zip")
    @ApiOperation(value = "Save invoices based on orders ref_key",
            notes = "Return invoices in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveInvoicesByOrderRefKeyList(@ApiParam(value = "Need order ref_key list to get invoices", required = true)
                                                                                             @RequestBody List<String> orderRefKeys) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + INVOICE_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeZipToResponse(out, orderRefKeys));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/bill/{ref}")
    @ApiOperation(value = "Save bill based on order ref key",
            notes = "Return bill",
            response = HttpEntity.class)
    public @ResponseBody HttpEntity<byte[]> saveBillByOrderRefKey(@ApiParam(value = "Need order ref_key list to get invoices", required = true)
                                                                  @PathVariable String ref) {
        ReportResult reportResult = billService.saveDocumentByOrderRefKey(ref);
        return toHttpEntity(reportResult);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/bill", produces = "application/zip")
    @ApiOperation(value = "Save bills based on orders ref_key",
            notes = "Return bills in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveBillsByOrderRefKeyList(@ApiParam(value = "Need order ref_key list to get bills", required = true)
                                                                                          @RequestBody List<String> orderRefKeys) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + BILL_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeBillZipToResponse(out, orderRefKeys));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/quality", produces = "application/zip")
    @ApiOperation(value = "Save quality documents based on orders ref_key",
            notes = "Return quality documents in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveQualityDocumentsByOrderRefKeyList(@ApiParam(value = "Need order ref_key list to get quality documents", required = true)
                                                                                                     @RequestBody List<String> orderRefKeys) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + QUALITY_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeQuantityZipToResponse(out, orderRefKeys));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGIST', 'DRIVER')")
    @GetMapping(value = "/save/quality/{ref}", produces = "application/zip")
    @ApiOperation(value = "Save quality documents based on order ref_key",
            notes = "Return quality documents in zip format",
            response = ResponseEntity.class)
    public @ResponseBody ResponseEntity<StreamingResponseBody> saveQualityDocumentsByOrderRefKey(@ApiParam(value = "Need order ref_key list to get quality documents", required = true)
                                                                                                 @PathVariable String ref) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT + HEADER_DELIMITER + HEADER_FILENAME_KEY + QUALITY_ZIP_FILENAME + ARCHIVE_EXTENSION)
                .body(out -> writeQuantityZipToResponse(out, List.of(ref)));
    }

    private HttpEntity<byte[]> toHttpEntity(ReportResult reportResult) {
        ContentDisposition contentDisposition = ContentDisposition.builder(ATTACHMENT)
                .filename(reportResult.name(), StandardCharsets.UTF_8)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(contentDisposition);
        return new HttpEntity<>(reportResult.report(), headers);
    }

    private void writeZipToResponse(OutputStream out, List<String> orderRefKeys) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            invoiceService.saveDocumentsByOrderRefKeys(zipOutputStream, orderRefKeys);
        }
    }

    private void writeBillZipToResponse(OutputStream out, List<String> orderRefKeys) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            billService.saveDocumentsByOrderRefKeys(zipOutputStream, orderRefKeys);
        }
    }

    private void writeQuantityZipToResponse(OutputStream out, List<String> orderRefKeys) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
            qualityService.saveDocumentsByOrderRefKeys(zipOutputStream, orderRefKeys);
        }
    }
}
