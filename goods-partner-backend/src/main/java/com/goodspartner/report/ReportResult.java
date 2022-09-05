package com.goodspartner.report;


import java.util.Arrays;
import java.util.Objects;

public record ReportResult(String name, byte[] report) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportResult that = (ReportResult) o;

        if (!Objects.equals(name, that.name)) return false;
        return Arrays.equals(report, that.report);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(report);
        return result;
    }

    @Override
    public String toString() {
        return "ReportResult{" +
               "name='" + name + '\'' +
               ", report=" + Arrays.toString(report) +
               '}';
    }
}
