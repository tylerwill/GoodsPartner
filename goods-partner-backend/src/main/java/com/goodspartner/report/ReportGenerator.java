package com.goodspartner.report;

import java.time.LocalDate;
import java.util.function.Consumer;

public interface ReportGenerator {
    default void generateReport(LocalDate date, Consumer<ReportResult> reportResultConsumer) {
        ReportResult reportResult = generateReport(date);
        reportResultConsumer.accept(reportResult);
    }

    ReportResult generateReport(LocalDate date);
}
