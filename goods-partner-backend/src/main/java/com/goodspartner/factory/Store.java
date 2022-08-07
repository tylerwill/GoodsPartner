package com.goodspartner.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Store {

    private String name;

    private String address;

}
