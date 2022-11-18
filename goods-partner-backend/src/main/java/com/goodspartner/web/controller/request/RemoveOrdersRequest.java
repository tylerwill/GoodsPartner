package com.goodspartner.web.controller.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RemoveOrdersRequest {

    private List<Integer> orderIds;

}
