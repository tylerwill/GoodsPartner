package com.goodspartner.action;

import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.exception.IllegalDeliveryStatusForOperation;
import com.goodspartner.exception.InvalidActionType;
import com.goodspartner.exception.NoRoutesFoundForDelivery;

import java.util.Arrays;
import java.util.List;

import static com.goodspartner.entity.DeliveryStatus.DRAFT;

public enum DeliveryAction {

    APPROVE {
        @Override
        public void perform(Delivery delivery) {

            if (delivery.getStatus() != DRAFT) {
                throw new IllegalDeliveryStatusForOperation(delivery, "approve");
            }

            List<Route> routes = delivery.getRoutes();
            if (routes.isEmpty()) {
                throw new NoRoutesFoundForDelivery(delivery);
            }

            delivery.setStatus(DeliveryStatus.APPROVED);
        }
    };

    public static DeliveryAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(Delivery routePoint);
}
