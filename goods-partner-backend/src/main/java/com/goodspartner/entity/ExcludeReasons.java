package com.goodspartner.entity;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExcludeReasons {

    DROPPED_ORDER("Замовлення %s вилучене алгоритмом в процессі розрахунку доставки"),
    DELETED_ORDER_EXCLUDE_REASON("Замовлення: %s має флаг видалення в 1С"),
    INVOICE_MISSED_EXCLUDE_REASON("Відсутня або видалена видаткова в 1С для замовлення: %s")
    ;

    private final String reasonTemplate;

    public String formatWithOrderNumber(String orderNumber) {
        return String.format(this.reasonTemplate, orderNumber);
    }

}
