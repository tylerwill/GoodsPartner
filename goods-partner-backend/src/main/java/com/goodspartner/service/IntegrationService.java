package com.goodspartner.service;

import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.OrderDto;

import java.time.LocalDate;
import java.util.List;

public interface IntegrationService {

    List<InvoiceDto> getInvoicesByOrderRefKeys(List<String> orderRefKeys);

    List<OrderDto> findAllByShippingDate(LocalDate date);

}