package com.goodspartner.service.document;

import com.goodspartner.entity.DeliveryType;
import com.goodspartner.service.dto.DocumentDto;

import java.util.UUID;


public interface DocumentPdfService {
    DocumentDto getPdfDocumentsByRoute(Long routeId);

    DocumentDto getPdfDocumentsByRoutePoint(Long routePointId);

    DocumentDto getPdfDocumentsByDeliveryId(UUID deliveryId, DeliveryType deliveryType);
}
