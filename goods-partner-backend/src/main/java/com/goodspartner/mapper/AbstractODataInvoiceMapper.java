package com.goodspartner.mapper;

import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.service.dto.external.grandedolce.ODataInvoiceDto;
import com.goodspartner.service.dto.external.grandedolce.ODataOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDate;

import static com.goodspartner.mapper.util.MapperUtil.getRoundedDouble;

@Mapper
public abstract class AbstractODataInvoiceMapper {
    @Named("mapDocumentDate")
    public String mapDocumentDate(LocalDate documentDate) {
        return getDateString(documentDate);
    }

    @Named("mapOrderDate")
    public String mapOrderDate(ODataOrderDto order) {
        return getDateString(order.getShippingDate());
    }

    @Named("mapCompanyInformation")
    public String mapCompanyInformation(ODataInvoiceDto oDataInvoiceDto) {
        return "п/р " + oDataInvoiceDto.getCompanyAccount() +
                " у банку " + oDataInvoiceDto.getBankName() +
                ", МФО " + oDataInvoiceDto.getMfoCode() +
                " " + oDataInvoiceDto.getAddress() +
                ", тел.: " + oDataInvoiceDto.getPhone() +
                ", код ЄДРПОУ " + oDataInvoiceDto.getOrganisationCodes().getEdrpouCode() +
                ", ІПН " + oDataInvoiceDto.getOrganisationCodes().getInnCode() +
                ", № свід. " + oDataInvoiceDto.getOrganisationCodes().getOrganisationNumberCode() +
                ", Є платником податку на прибуток на загальних підставах";
    }

    @Named("mapOrderInfo")
    public String mapOrderInfo(ODataInvoiceDto oDataInvoiceDto) {
        String dateString = getDateString(oDataInvoiceDto.getOrder().getShippingDate());
        return "Замовлення покупця № " + Integer.parseInt(oDataInvoiceDto.getOrder().getOrderNumber()) + " від " + dateString;
    }

    @Named("mapInvoiceAmountPDV")
    public Double mapInvoiceAmountPDV(ODataInvoiceDto oDataInvoiceDto) {
        Double invoiceAmountPdv = oDataInvoiceDto.getProducts().stream()
                .map(InvoiceProduct::getPriceAmountPDV)
                .reduce(0d, Double::sum);
        return getRoundedDouble(invoiceAmountPdv);
    }

    private String getDateString(LocalDate documentDate) {
        int year = documentDate.getYear();
        int day = documentDate.getDayOfMonth();
        int month = documentDate.getMonthValue();
        return day + " " + getUkrMonth(month) + " " + year + " р.";
    }

    private String getUkrMonth(int month) {
        return switch (month) {
            case 1 -> "січня";
            case 2 -> "лютого";
            case 3 -> "березня";
            case 4 -> "квітня";
            case 5 -> "травня";
            case 6 -> "червня";
            case 7 -> "липня";
            case 8 -> "серпня";
            case 9 -> "вересня";
            case 10 -> "жовтня";
            case 11 -> "листопада";
            default -> "грудня";
        };
    }
}
