package com.goodspartner.service.document;

import java.util.zip.ZipOutputStream;

public interface DocumentService {
    void saveDocumentsByRouteId(ZipOutputStream zipOutputStream, Long routeId);

    void saveDocumentByRoutePointId(ZipOutputStream zipOutputStream, Long routePointId);
}
