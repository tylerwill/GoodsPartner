package com.goodspartner.service.document;

import com.goodspartner.service.dto.DocumentDto;


public interface DocumentPdfService {
    DocumentDto getPdfDocumentsByRoute(Long routeId);

    DocumentDto getPdfDocumentsByRoutePoint(Long routePointId);
}
