package com.goodspartner.web.localization.interfaces;

import com.goodspartner.entity.DeliveryType;

public interface Interpreter {
    String enumToString(DeliveryType deliveryType);
    DeliveryType stringToEnum(String deliveryTypeFromFrontend);
}
