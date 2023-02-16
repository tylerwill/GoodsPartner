package com.goodspartner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDto {

    private int id;
    private String description;
    private LocalDate executionDate;
    private MapPoint mapPoint;
    private CarDto car;

}
