package com.goodspartner.service.document.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSheet {
    private String clientName;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime expectedArrival;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime expectedCompletion;
    private String comment;
    private String invoiceAmount;
    private String invoiceNumber;
}
