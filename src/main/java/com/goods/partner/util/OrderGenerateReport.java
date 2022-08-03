package com.goods.partner.util;

import com.goods.partner.dto.CalculationOrdersDto;
import com.goods.partner.dto.OrderDto;
import com.goods.partner.dto.ProductDto;
import com.goods.partner.entity.Order;
import com.goods.partner.entity.OrderedProduct;
import com.goods.partner.entity.Product;
import com.goods.partner.entity.projection.StoreProjection;
import com.goods.partner.repository.OrderRepository;
import com.goods.partner.repository.StoreRepository;
import com.goods.partner.service.impl.DefaultOrderService;
import lombok.Builder;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderGenerateReport {

    @Autowired
    private DefaultOrderService defaultOrderService;

    public void generateExcelFile(LocalDate date) throws IOException {
        File file = new File("resources/util/Order_Report.xlsx");

//        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);

            Row row = sheet.getRow(0);
            row.getCell(1).setCellValue(date);

            CalculationOrdersDto calculationOrdersDto = defaultOrderService.calculateOrders(date);


            int ordersQuantity = calculationOrdersDto.getOrders().size();
            sheet.shiftRows(3-4, 5-6, ordersQuantity);

            int rowNumber = 3;
            int orderCount = 0;
            Row currentRow = sheet.getRow(rowNumber);

            for (OrderDto orderDto : calculationOrdersDto.getOrders()) {

                List<ProductDto> productDtos = orderDto.getOrderData().getProducts();
                sheet.shiftRows(++rowNumber, rowNumber, productDtos.size() - 1);

                int productCount = 0;

                currentRow.getCell(0).setCellValue(orderCount++);
                currentRow.getCell(1).setCellValue(orderDto.getOrderNumber());

                for (ProductDto productDto : productDtos) {

                    currentRow.getCell(2).setCellValue(productCount++);
                    currentRow.getCell(3).setCellValue(productDto.getProductName());
                    currentRow.getCell(4).setCellValue(productDto.getUnitWeight());
                    currentRow.getCell(5).setCellValue(productDto.getAmount());
                    currentRow.getCell(6).setCellValue(productDto.getTotalProductWeight());

                    currentRow = currentRow.iterator().next().getRow();
                }

                currentRow.getCell(1).setCellValue(orderDto.getOrderData().getOrderWeight());
                currentRow = currentRow.iterator().next().getRow();
            }

            currentRow.getCell(1).setCellValue(calculationOrdersDto.getOrders()
                    .stream()
                    .mapToDouble(e -> e.getOrderData().getOrderWeight())
                    .sum());

//        }

        fileInputStream.close();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
    }

//    private List<OrderGenerateReport.ExcelFormatDto> getOrdersByDate(LocalDate date) {
//        List<Order> ordersByDate = orderRepository.findAllByShippingDateEquals(date);
//
//        List<OrderGenerateReport.ExcelFormatDto> excelFormatDtos = new ArrayList<>();
//        for (Order order : ordersByDate) {
//            excelFormatDtos.add(ExcelFormatDto.builder()
//                    .orderNumber(order.getNumber())
//                    .orderedProducts(order.getOrderedProducts())
//                    .build()
//            );
//        }
//
//        return excelFormatDtos;
//    }
//
//    @Getter
//    @Builder
//    private static class ExcelFormatDto {
//        private int orderNumber;
//        private List<OrderedProduct> orderedProducts;
////        private double unitWeight;
////        private int productAmount;
////        private double totalProductWeight;
////        private double totalOrderWeight;
//    }
}
