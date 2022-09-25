package com.goodspartner.report;

import java.util.UUID;

public interface ReportGenerator {

    ReportResult generateReport(UUID deliveryId);

}
