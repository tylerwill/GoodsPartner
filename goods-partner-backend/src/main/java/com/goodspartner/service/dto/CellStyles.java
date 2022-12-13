package com.goodspartner.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CellStyles {
    MAIN_HEADER("MAIN_HEADER"),
    HEADER("HEADER"),
    HEADER_ORGANISATION("HEADER_ORGANISATION"),
    HEADER_BOLD("HEADER_BOLD"),
    TABLE_CELL_1("TABLE_CELL_1"),
    TABLE_CELL_2("TABLE_CELL_2"),
    TABLE_CELL_3("TABLE_CELL_3"),
    TABLE_CELL_4("TABLE_CELL_4"),
    TABLE_CELL_5("TABLE_CELL_5"),
    TABLE_CELL_6("TABLE_CELL_6"),
    TABLE_CELL_7("TABLE_CELL_7"),
    TABLE_CELL_8("TABLE_CELL_8"),
    TABLE_CELL_9("TABLE_CELL_9"),
    BILL_TABLE_CELL_2("BILL_TABLE_CELL_2"),
    BILL_TABLE_CELL_3("BILL_TABLE_CELL_3"),
    BILL_TABLE_CELL_4("BILL_TABLE_CELL_4"),
    BILL_TABLE_CELL_5("BILL_TABLE_CELL_5"),
    BILL_TABLE_CELL_6("BILL_TABLE_CELL_6"),
    AMOUNT_SIGNATURE("AMOUNT_SIGNATURE"),
    RESULT_SIGNATURE("RESULT_SIGNATURE"),
    WORD_AMOUNT_DEFINITION("WORD_AMOUNT_DEFINITION"),
    RESPONSIBLE_SIGNATURE("RESPONSIBLE_SIGNATURE");

    private final String cellStyle;

}
