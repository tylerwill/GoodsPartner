package com.goodspartner.report;


import com.goodspartner.entity.DeliveryType;

import java.util.UUID;

public interface ReportGenerator {

    ReportResult generateReport(UUID deliveryId, DeliveryType loadType);

}
