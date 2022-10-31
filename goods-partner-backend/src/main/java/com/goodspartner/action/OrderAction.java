package com.goodspartner.action;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.entity.Route;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.InvalidActionType;
import com.goodspartner.exception.NoRoutesFoundForDelivery;

import java.util.Arrays;
import java.util.List;

import static com.goodspartner.entity.DeliveryStatus.DRAFT;

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
    };

    public static OrderAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(OrderExternal order);
}
