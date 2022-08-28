package com.goodspartner.report;

import com.goodspartner.dto.CarDto;
import com.goodspartner.dto.ProductDto;
import com.goodspartner.web.controller.response.RoutesCalculation;
import com.google.common.annotations.VisibleForTesting;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;

import static com.goodspartner.report.ReportUtils.copyCells;
import static java.util.stream.Collectors.*;

@Service
public class CarLoadSheetGenerator {

    private static final int FIRST_INSERTED_ROW = 4;
    private static final String NO_ORDERS = "НА ОБРАНУ ДАТУ ЗАВАНТАЖЕНЬ НЕМАЄ";
    private static final String TABLE_TITTLE = "Звіт по завантаженню %s (%s) товарами";
    private static final String IN_TOTAL = "Всього";

    @SneakyThrows
    public ByteArrayOutputStream generateSheet(XSSFSheet sheet, RoutesCalculation routesCalculation, LocalDate date) {
        ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();

        if (routesCalculation.getCarLoadDetails().isEmpty()) {
            sheet.getRow(7).createCell(3).setCellValue(NO_ORDERS);
        }
        for (RoutesCalculation.CarLoadDto carLoadDetail : routesCalculation.getCarLoadDetails()) {
            int quantity = carLoadDetail.getOrders().size();

            Row templateProductRow = sheet.getRow(3);

            addRows(sheet, templateProductRow, quantity);
            addRowsForProductsAndFill(sheet, routesCalculation, templateProductRow, date);
        }

        return arrayStream;
    }

    private void addRowsForProductsAndFill(XSSFSheet sheet, RoutesCalculation routesCalculation, Row sourceProductRow, LocalDate date) {
        int rowNumber = 3;
        int orderCount = 1;
        Row currentRow = sheet.getRow(rowNumber);
        String sheetName = sheet.getSheetName();

        for (RoutesCalculation.CarLoadDto carLoadDetail : routesCalculation.getCarLoadDetails()) {
            CarDto car = carLoadDetail.getCar();
            String tittle = String.format(TABLE_TITTLE, car.getName(), car.getLicencePlate());
            sheet.getRow(1).getCell(0).setCellValue(tittle);

            Map<String, List<Pair<String, ProductDto>>> groupedProducts = sheetName.equals("products_in_car") ?
                    groupByProducts(carLoadDetail) : groupByOrders(carLoadDetail);

            double carLoadWeight = 0;
            for (Map.Entry<String, List<Pair<String, ProductDto>>> groupedProduct : groupedProducts.entrySet()) {
                List<Pair<String, ProductDto>> pairList = groupedProduct.getValue();
                if (pairList.size() > 1) {
                    sheet.shiftRows(rowNumber + 1, sheet.getLastRowNum(), pairList.size() - 1);
                    sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + pairList.size() - 1, 0, 0));
                    sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber + pairList.size() - 1, 1, 1));
                }
                currentRow.setHeight((short) -1);
                currentRow.getCell(0).setCellValue(orderCount++);
                currentRow.getCell(1).setCellValue(groupedProduct.getKey());

                double productWeight = 0;

                for (Pair<String, ProductDto> productPair : pairList) {
                    if (currentRow == null) {
                        currentRow = sheet.createRow(rowNumber);
                        copyCells(sourceProductRow, currentRow);
                    }
                    currentRow.getCell(2).setCellValue(productPair.getFirst());
                    currentRow.getCell(3).setCellValue(productPair.getSecond().getUnitWeight());
                    currentRow.getCell(4).setCellValue(productPair.getSecond().getAmount());
                    currentRow.getCell(5).setCellValue(productPair.getSecond().getTotalProductWeight());

                    productWeight += productPair.getSecond().getTotalProductWeight();
                    rowNumber++;
                    currentRow = sheet.getRow(rowNumber);
                }
                currentRow = sheet.getRow(rowNumber);
                currentRow.getCell(3).setCellValue(IN_TOTAL);
                currentRow.getCell(4).setCellValue(getAllProductAmount(pairList));
                currentRow.getCell(5).setCellValue(productWeight);
                rowNumber++;
                sheet.shiftRows(rowNumber, sheet.getLastRowNum(), 1);
                rowNumber++;
                currentRow = sheet.getRow(rowNumber);

                carLoadWeight += productWeight;
            }

            currentRow.getCell(5).setCellValue(carLoadWeight);
        }

        rowNumber++;
        currentRow = sheet.getRow(rowNumber);

        String formattedDate = date.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.SHORT));
        currentRow.getCell(5).setCellValue(formattedDate);
    }

    private void addRows(XSSFSheet sheet, Row sourceProductRow, int quantity) {
        if (quantity > 1) {
            sheet.shiftRows(FIRST_INSERTED_ROW, sheet.getLastRowNum(), (quantity - 1) * 2);

            for (int i = FIRST_INSERTED_ROW; i < FIRST_INSERTED_ROW + (quantity - 1) * 2; i++) {
                Row sourceWeightRow = sheet.getRow(FIRST_INSERTED_ROW + (quantity - 1) * 2);
                Row newRow = sheet.createRow(i);
                if (i % 2 == 0) {
                    ReportUtils.copyCells(sourceWeightRow, newRow);
                } else {
                    ReportUtils.copyCells(sourceProductRow, newRow);
                }
            }
        }
    }

    @VisibleForTesting
    Map<String, List<Pair<String, ProductDto>>> groupByOrders(RoutesCalculation.CarLoadDto carLoadDetail) {
        return carLoadDetail.getOrders().stream()
                .flatMap(order -> order.getProducts().stream()
                        .map(product -> Pair.of(order.getOrderNumber(), Pair.of(product.getProductName(), product)))
                )
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, toList())));
    }

    @VisibleForTesting
    Map<String, List<Pair<String, ProductDto>>> groupByProducts(RoutesCalculation.CarLoadDto carLoadDetail) {
        return carLoadDetail.getOrders().stream()
                .flatMap(order -> order.getProducts().stream()
                        .map(product -> Pair.of(product.getProductName(),
                                Pair.of(order.getOrderNumber(), product)))
                )
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, toList())));
    }

    @VisibleForTesting
    int getAllProductAmount(List<Pair<String, ProductDto>> groupedProducts) {
        return groupedProducts.stream()
                .mapToInt(product -> product.getSecond().getAmount())
                .sum();
    }
}
