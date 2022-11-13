package com.goodspartner.action;

import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.InvalidActionType;

import java.util.Arrays;

public enum OrderAction {

    RESCHEDULE {
        @Override
        public void perform(OrderExternal order) {
            order.setDelivery(null);
            order.setDeliveryStart(null);
            order.setDeliveryFinish(null);
            order.setCarLoad(null);
            order.setDropped(false);
            order.setExcluded(false);
        }
    };

    public static OrderAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(OrderExternal order);
}
