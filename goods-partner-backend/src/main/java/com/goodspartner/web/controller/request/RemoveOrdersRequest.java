package com.goodspartner.web.controller.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class RemoveOrdersRequest {

    private List<Long> orderIds;

}
