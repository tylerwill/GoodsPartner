package com.goodspartner.service.document;

import java.io.OutputStream;

public interface DocumentPdfService {
    OutputStream getPdfDocumentsByRoute(Long routeId);

    OutputStream getPdfDocumentsByRoutePoint(Long routePointId);
}
