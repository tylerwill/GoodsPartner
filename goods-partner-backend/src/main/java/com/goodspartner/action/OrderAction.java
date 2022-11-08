package com.goodspartner.action;

import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.InvalidActionType;

import java.util.Arrays;
import java.util.List;

public enum OrderAction {

    SCHEDULE {
        @Override
        public void perform(OrderExternal order) {
            order.setDelivery(null);
            order.setDeliveryStart(null);
            order.setDeliveryFinish(null);
            order.setCarLoad(null);
            order.setDropped(false);
            order.setExcluded(false);
        }

        @Override
        public void performForList(List<OrderExternal> orderExternals) {
            orderExternals.forEach(this::perform);
        }
    };

    public static OrderAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(OrderExternal order);

    public abstract void performForList(List<OrderExternal> orderExternals);
}
