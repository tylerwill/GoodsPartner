package com.goodspartner.service.util;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.service.dto.DocumentPdfType;
import com.goodspartner.service.dto.PdfDocumentDto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public abstract class AbstractBaseDocumentPdfTest {
    protected static final String HTML_TEMPLATE_BILL = "bill_template_new.html";
    protected static final String HTML_TEMPLATE_INVOICE = "invoice_template_new.html";
    protected static final String BILL_EXPECTED = "documents/bill-expected.html";
    protected static final String INVOICE_EXPECTED = "documents/invoice-expected.html";

    protected InvoiceDto createInvoiceDto(List<InvoiceProduct> invoiceProducts) {
        return InvoiceDto.builder()
                .edrpouCode("8546987654")
                .companyAccount("12345678912346")
                .bankName("ПРАТ Приват Банк")
                .mfoCode("789456")
                .number("1987")
                .documentDate("25.02.2023")
                .deliveryAddress("м. Днапро, вул. Набережна Перемоги, буд. 55-А")
                .storeAddress("м. Днапро, вул. Набережна Перемоги, буд. 55-А")
                .orderNumber("789")
                .orderDate("23.02.2022")
                .orderInfo("Тут інформація щодо замовлення")
                .companyName("МЕГА ФУД ДЕЛИВЕРИ")
                .companyInformation("ТОВ «НОВА ПОШТА»\n" +
                        "Місцезнаходження/поштова адреса:\n" +
                        "03026, м. Київ, Столичне шосе, 103, корпус 1, поверх 9. код ЄДРПОУ 31316718\n" +
                        "IBAN UA 533314670000026005300918092 в АТ «Ощадбанк»\n" +
                        " ІПН 313167116014\n" +
                        "Свідоцтво платника ПДВ № 100148005")
                .clientName("ТОВ СІЛЬПО")
                .clientContract("20220102-ПФ-РП від 20.05.2019 року")
                .products(invoiceProducts)
                .invoiceAmount(55_25897.55)
                .invoiceAmountPDV(155_25897.55)
                .invoiceAmountWithoutPDV(155_25897.00)
                .textNumeric("Просто текст")
                .managerFullName("Трегубенко С.О.")
                .build();
    }

    protected List<InvoiceProduct> createProductsWithQualityDocuments() {
        return List.of(
                InvoiceProduct.builder().lineNumber("1").productName("Свинина").totalProductWeight("100").measure("кг.").price("55.20").priceAmount("15 000.55").uktzedCode("987654").priceWithoutPDV("15 000.55").amountWithoutPDV("15 000.55").qualityUrl("documents/agreement.pdf").build(),
                InvoiceProduct.builder().lineNumber("2").productName("Говядина").totalProductWeight("200").measure("кг.").price("155.20").priceAmount("255 000.20").uktzedCode("987654").priceWithoutPDV("15 000.55").amountWithoutPDV("15 000.55").qualityUrl("documents/merry.jpg").build(),
                InvoiceProduct.builder().lineNumber("3").productName("Курятина").totalProductWeight("300").measure("кг.").price("255.20").priceAmount("55 000.20").uktzedCode("987654").priceWithoutPDV("15 000.55").amountWithoutPDV("15 000.55").qualityUrl("documents/merry34.png").build()
        );
    }

    protected List<InvoiceProduct> createProductsQualityDocumentsAreAbsent() {
        return List.of(
                InvoiceProduct.builder().lineNumber("1").productName("Свинина").totalProductWeight("100").measure("кг.").price("55.20").priceAmount("15 000.55").uktzedCode("987654").priceWithoutPDV("15 000.55").amountWithoutPDV("15 000.55").qualityUrl(null).build(),
                InvoiceProduct.builder().lineNumber("2").productName("Говядина").totalProductWeight("200").measure("кг.").price("155.20").priceAmount("255 000.20").uktzedCode("987654").priceWithoutPDV("15 000.55").amountWithoutPDV("15 000.55").qualityUrl("").build(),
                InvoiceProduct.builder().lineNumber("3").productName("Курятина").totalProductWeight("300").measure("кг.").price("255.20").priceAmount("55 000.20").uktzedCode("987654").priceWithoutPDV("15 000.55").amountWithoutPDV("15 000.55").qualityUrl("").build()
        );
    }

    protected PdfDocumentDto createPdfDocumentDto(String bill, String invoice) {
        Map<DocumentPdfType, Set<String>> qualityDocuments = new HashMap<>();
        qualityDocuments.put(DocumentPdfType.PDF, Set.of("documents/pdf/pdf-to-insert.pdf"));
        qualityDocuments.put(DocumentPdfType.IMAGES, Set.of("documents/images/car.jpg", "documents/images/car.jpeg", "documents/images/car.png", "documents/images/car.bmp"));

        return PdfDocumentDto.builder()
                .bill(bill)
                .invoice(invoice)
                .qualityDocuments(qualityDocuments)
                .build();
    }

    protected String getExpectedHtml(String nameOfExpectedHtml) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(nameOfExpectedHtml)) {
            Objects.requireNonNull(inputStream);
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeExpectedFile(byte[] expectedFile, String fileNameWithExtension) {
        String pathToWrite = "src/test/resources/" + fileNameWithExtension;
        try (FileOutputStream resultWriter = new FileOutputStream(pathToWrite)) {
            resultWriter.write(expectedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
